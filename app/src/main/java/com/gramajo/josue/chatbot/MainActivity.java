package com.gramajo.josue.chatbot;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gramajo.josue.chatbot.Adapters.ChatAdapter;
import com.gramajo.josue.chatbot.Objects.Message;
import com.gramajo.josue.chatbot.Objects.Node;
import com.gramajo.josue.chatbot.Objects.ObjectLists.Messages;
import com.gramajo.josue.chatbot.Utils.DecisionTree;
import com.gramajo.josue.chatbot.Utils.FirebaseUtils;
import com.gramajo.josue.chatbot.Utils.GlobalAccess;
import com.gramajo.josue.chatbot.Utils.JsonUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ListView chatListView;
    private ChatAdapter adapter;
    private TextView chatState, chatName;

    private Messages chatHistory;

    private String currentMessage = "";

    SharedPreferences settings;

    private String evaluationTreeID = "";
    private Node evaulationTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        messageEditText = (EditText) findViewById(R.id.et_message);

        chatState = (TextView) findViewById(R.id.tv_chatbot_state);

        chatListView = (ListView) findViewById(R.id.lv_chat);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_send_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageEditText.getText().toString();
                if(!text.equals("")){
                    if(GlobalAccess.USER.equals("")){
                        requestUser();
                    }else{
                        sendMessage(true, text);
                    }
                }
            }
        });

        chatHistory = new Messages();

        adapter = new ChatAdapter(MainActivity.this, new ArrayList<Message>());
        chatListView.setAdapter(adapter);

        settings = getSharedPreferences("HAL_CHAT_BOT", Context.MODE_PRIVATE);
        GlobalAccess.USER = settings.getString("tester_user","");
        if(GlobalAccess.USER.equals("")){
            requestUser();
        }else{
            FirebaseUtils.INSTANCE.checkForExistingTestingUser();
        }

        checkForExistingMessages();

        GlobalAccess.TREE = DecisionTree.INSTANCE.generateTree();

        //FirebaseUtils.INSTANCE.retrieveDecisionTree(this);
    }

    private void requestUser(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);

        final EditText edittext = new EditText(this);
        edittext.setLayoutParams(lp);

        alert.setMessage("Ingresar usuario");
        alert.setTitle("Favor de ingresar un nombre de usuario para iniciar las pruebas");

        container.addView(edittext);

        alert.setView(container);

        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = edittext.getText().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("tester_user", value);
                editor.commit();

                GlobalAccess.USER = value;

                FirebaseUtils.INSTANCE.checkForExistingTestingUser();
            }
        });

        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_messages) {
            JsonUtil.INSTANCE.copyJsonContentToClipboard(this, JsonUtil.FILE_TYPE.MESSAGES);
            return true;
        }else if (id == R.id.action_questions){
            JsonUtil.INSTANCE.copyObjectAsJsonString(GlobalAccess.TREE, this);
            return true;
        }else if(id == R.id.update_tree){
            FirebaseUtils.INSTANCE.retrieveDecisionTree(this);
            return true;
        }else if(id == R.id.update_firestore){
            if(!GlobalAccess.USER.equals("josue")){
                Toast.makeText(this, "No tenes los privilegios administrativos chavo ;)", Toast.LENGTH_SHORT).show();
                return false;
            }
            DecisionTree.INSTANCE.saveTree();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(boolean selfMessage, String strMessage){
        Message message = new Message();
        message.setId(GlobalAccess.MESSAGE_ID++);
        message.setMessage(strMessage);
        message.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        message.setSelfMessage(selfMessage);

        chatHistory.addMessage(message);

        currentMessage = strMessage;

        JsonUtil.INSTANCE.writeJson(this, chatHistory, JsonUtil.FILE_TYPE.MESSAGES);

        displayMessage(message);

        if(selfMessage){
            messageEditText.setText("");
            new RespondeAsynchronously(false).execute();
        }
    }

    public void displayMessage(Message message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scrollToLastMessage();
    }

    private void checkForExistingMessages(){
        try{
            Messages readed = JsonUtil.INSTANCE.readJSON(this, JsonUtil.FILE_TYPE.MESSAGES, Messages.class);
            for(Message m : readed.getMessages()){
                displayMessage(m);
                chatHistory.addMessage(m);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void scrollToLastMessage() {
        chatListView.setSelection(chatListView.getCount() - 1);
    }

    private String decideResponse(){
        String text = this.currentMessage.toLowerCase().trim();
        text = removeAccent(text);

        if(GlobalAccess.TREE == null) GlobalAccess.TREE = DecisionTree.INSTANCE.generateTree();

        if(evaulationTree == null) evaulationTree = GlobalAccess.TREE;

        if(evaluationTreeID == ""){
            if(validateNode(evaulationTree, text)){
                return changeValues(evaulationTree.getResponse());
            }else{
                for(Node n : evaulationTree.getChildren()){
                    if(validateNode(n, text)){
                        if(n.getChildren() != null){
                            evaluationTreeID = n.getId();
                        }

                        return changeValues(n.getResponse());
                    }
                }
            }
        }else{
            searchEvaluationTree(GlobalAccess.TREE);

            for(Node n : evaulationTree.getChildren()){
                if (n.getKey() != null){
                    String rec = estimateRecommendation();
                    return rec + n.getKey();
                }else if(validateNode(n, text)){
                    if(!n.getDecisionType().equals("repeat")){
                        evaluationTreeID = n.getId();
                        if(n.getChildren() == null){
                            evaulationTree = null;
                            evaluationTreeID = "";
                        }
                    }
                    int leve = n.getLevel();
                    System.out.println(n.getLevel());

                    switch (n.getLevel()){
                        case 3:
                            GlobalAccess.ZONE = Integer.parseInt(text);
                            break;
                        case 4:
                            GlobalAccess.BUDGET = Float.parseFloat(text);
                            break;
                        case 5:
                            GlobalAccess.TIME = text;
                            break;
                    }

                    return changeValues(n.getResponse());
                }
            }


        }

        FirebaseUtils.INSTANCE.saveUnansweredQuestion(text);
        return "Lo siento, no podemos generar una recomendacion con los datos actuales";
    }

    private String estimateRecommendation(){
        StringBuilder recomendation = new StringBuilder();
        recomendation.append("Nuestra recomendacion es:\n");

        if (Arrays.asList(1,2,6).contains(GlobalAccess.ZONE)){
            recomendation.append("Segun su ubicacion: Universidad Mariano Galvez de Guatemala\n");
        }else if (Arrays.asList(8,9,5).contains(GlobalAccess.ZONE)){
            recomendation.append("Segun su ubicacion: Universidad Fransisco Marroquin de Guatemala\n");
        }else if (Arrays.asList(10,15,16).contains(GlobalAccess.ZONE)){
            recomendation.append("Segun su ubicacion: Universidad Del Valle de Guatemala\n");
        }else if (Arrays.asList(11,12,13).contains(GlobalAccess.ZONE)){
            recomendation.append("Segun su ubicacion: Universidad San Carlos de Guatemala\n");
        }

        if (GlobalAccess.BUDGET >= 0 && GlobalAccess.BUDGET <= 100){
            recomendation.append("Segun su presupuesto: Universidad San Carlos de Guatemala\n");
        }else if (GlobalAccess.BUDGET >= 900 && GlobalAccess.BUDGET <= 1500){
            recomendation.append("Segun su presupuesto: Universidad Mariano Galvez de Guatemala\n");
        }else if (GlobalAccess.BUDGET >= 3000 && GlobalAccess.BUDGET <= 4000){
            recomendation.append("Segun su presupuesto: Universidad Del Valle de Guatemala\n");
        }else if (GlobalAccess.BUDGET >= 4001 && GlobalAccess.BUDGET <= 7000){
            recomendation.append("Segun su presupuesto: Universidad Fransisco Marroquin de Guatemala\n");
        }

        if (GlobalAccess.TIME.toLowerCase().equals("mañana")){
            recomendation.append("Jornada: Matutina\n");
        }else if (GlobalAccess.TIME.toLowerCase().equals("tarde")){
            recomendation.append("Jornada: Vespertina\n");
        }else if (GlobalAccess.TIME.toLowerCase().equals("noche")){
            recomendation.append("Jornada: Noche\n");
        }

        return recomendation.toString();
    }

    private String changeValues(String value){
        String newValue = value;

        newValue = newValue.replace("|random_number|", String.valueOf(new Random().nextInt(10000)));

        return newValue;
    }

    private void searchEvaluationTree(Node node){
        //Esta funcion recursiva busca el nodo el cual su id sea igual a evaluationTreeID,
        //  esto quiere decir que encontrara el nodo sobre el cual el chatbot realizo la pregunta anterior
        if(node.getId().equals(evaluationTreeID)){
            evaulationTree = node;
        }else{
            if(node.getChildren() != null){
                for(Node n : node.getChildren()){
                    searchEvaluationTree(n);
                }
            }
        }
    }

    private boolean validateNode(Node node, String text){
        switch (node.getDecisionType()){
            case "contains":
                for(String s : node.getKeyWords()){
                    if(text.contains(s)){
                        return true;
                    }
                }
                break;
            case "contains_all":
                for(String s : node.getKeyWords()){
                    if(text.indexOf(s) < 0){
                        return false;
                    }
                }
                return true;
            case "card":
                return isNumber(text);
            case "text":
                return true;
            case "repeat": return true;
            case "default": return true;
        }

        return false;
    }

    private boolean isNumber(String str){
        try{
            Float.parseFloat(str);
            return true;
        }catch (NumberFormatException nfe){
            return false;
        }
    }

    private String removeAccent(String text){
        String newText = text;
        newText = newText.replace("á","a");
        newText = newText.replace("é","e");
        newText = newText.replace("í","i");
        newText = newText.replace("ó","o");
        newText = newText.replace("ú","u");
        newText = newText.replace("ü","u");
        return newText;
    }

    private class RespondeAsynchronously extends AsyncTask<String, Void, String> {

        boolean isWriting = false;

        public RespondeAsynchronously(boolean isWriting){
            this.isWriting = isWriting;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                int millis = isWriting ? 3000 : 1000;
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if(isWriting){
                chatState.setText("En Linea");
                sendMessage(false, decideResponse());
            }else{
                chatState.setText("Escribiendo...");
                new RespondeAsynchronously(true).execute();
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
