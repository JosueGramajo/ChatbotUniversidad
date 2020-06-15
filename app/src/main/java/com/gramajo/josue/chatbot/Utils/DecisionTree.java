package com.gramajo.josue.chatbot.Utils;

import com.gramajo.josue.chatbot.Objects.Node;

/**
 * Created by josuegramajo on 4/17/18.
 */

public class DecisionTree {
    public static DecisionTree INSTANCE = new DecisionTree();

    private final String CONTAINS = "contains";
    private final String CONTAINS_ALL  = "contains_all";
    private final String CARD = "card";
    private final String TEXT = "text";
    private final String REPEAT = "repeat";
    private final String DEFAULT = "default";

    public void saveTree(){
        FirebaseUtils.INSTANCE.saveTreeInFirestore(generateTree());
    }

    public Node generateTree(){
        Node masterNode = new Node();
        masterNode.setDecisionType(CONTAINS);
        masterNode.addKeyWord("buenos dias");
        masterNode.addKeyWord("buenos días");
        masterNode.addKeyWord("buen dia");
        masterNode.addKeyWord("buen día");
        masterNode.addKeyWord("hola");
        masterNode.setLevel(1);
        masterNode.setResponse("Buen dia, como puedo ayudarle?");

        Node validationFailed = new Node();
        validationFailed.setDecisionType(REPEAT);
        validationFailed.setResponse("El numero de tarjeta ingresado es invalido, favor de tratar nuevamente");
        validationFailed.setLevel(3);

        Node validationDenied = new Node();
        validationDenied.setDecisionType(CONTAINS);
        validationDenied.addKeyWord("no");
        validationDenied.addKeyWord("negativo");
        validationDenied.addKeyWord("nel");
        validationDenied.setLevel(3);
        validationDenied.setResponse("Entendido, alguna otra cuestion con la cual pueda ayudarle?");

        Node exit = new Node();
        exit.setDecisionType(CONTAINS);
        exit.addKeyWord("otra pregunta");
        exit.addKeyWord("nueva pregunta");
        exit.addKeyWord("otra cosa");
        exit.setLevel(3);
        exit.setResponse("Alguna otra cuestion con la cual pueda ayudarle?");

        //****************************************************************
        Node node1 = new Node();
        node1.setDecisionType(CONTAINS_ALL);
        node1.addKeyWord("recomendar");
        node1.addKeyWord("universidad");
        node1.setLevel(2);
        node1.setResponse("Con mucho gusto puedo le puedo recomendar una universidad al responder una serie de preguntas, en que zona de la capital vive?");

        Node node2 = new Node();
        node2.setDecisionType(CARD);
        node2.setLevel(3);
        node2.setResponse("Cual es su presupuesto para gasto en colegiatura mensualmente? (En quetzales)");

        Node node3 = new Node();
        node3.setDecisionType(CARD);
        node3.setLevel(4);
        node3.setResponse("Usted posee tiempo disponible en la mañana, tarde o noche?");

        //**********************MEDICINA*************************************
        Node node4 = new Node();
        node4.setDecisionType(TEXT);
        node4.setLevel(5);
        node4.setResponse("Posee usted aptitudes para la lectura y redacción, ademas de la vocacion de ayudar a las personas?");

        Node node4_1 = new Node();
        node4_1.setDecisionType(CONTAINS);
        node4_1.addKeyWord("si");
        node4_1.addKeyWord("afirmativo");
        node4_1.addKeyWord("en efecto");
        node4_1.setLevel(7);
        node4_1.setResponse("Tiene usted estabilidad en las manos y soporta la sangre?");

            Node node4_1_1 = new Node();
            node4_1_1.setDecisionType(CONTAINS);
            node4_1_1.addKeyWord("si");
            node4_1_1.setLevel(7);
            node4_1_1.setKey("Facultad de Medicina, Carrera Medico Cirujano");
            node4_1_1.setResponse("");

        Node node4_2 = new Node();
        node4_2.setDecisionType(CONTAINS);
        node4_2.addKeyWord("no");
        node4_2.setLevel(7);
        node4_2.setResponse("Tiene facilidad para comunicarse con los niños?");

            Node node4_2_1 = new Node();
            node4_2_1.setDecisionType(CONTAINS);
            node4_2_1.addKeyWord("si");
            node4_2_1.setLevel(7);
            node4_2_1.setKey("Facultad de Medicina, Carrera Pediatria");
            node4_2_1.setResponse("");

        Node node4_3 = new Node();
        node4_3.setDecisionType(CONTAINS);
        node4_3.addKeyWord("no");
        node4_3.setLevel(7);
        node4_3.setResponse("Le gusta tratar de entender y ayudar a la gente a traves de palabras y razonamiento?");

            Node node4_3_1 = new Node();
            node4_3_1.setDecisionType(CONTAINS);
            node4_3_1.addKeyWord("si");
            node4_3_1.setLevel(7);
            node4_3_1.setKey("Facultad de Medicina, Carrera Psicologia");
            node4_3_1.setResponse("");

        Node node4_4 = new Node();
        node4_4.setDecisionType(CONTAINS);
        node4_4.addKeyWord("no");
        node4_4.setLevel(7);
        node4_4.setResponse("Le gustan los dientes?");

            Node node4_4_1 = new Node();
            node4_4_1.setDecisionType(CONTAINS);
            node4_4_1.addKeyWord("si");
            node4_4_1.setLevel(7);
            node4_4_1.setKey("Facultad de Medicina, Carrera Odontologia");
            node4_4_1.setResponse("");

        node4_4.addChildren(node4_4_1);

        node4_3.addChildren(node4_4);
        node4_3.addChildren(node4_3_1);

        node4_2.addChildren(node4_3);
        node4_2.addChildren(node4_2_1);

        node4_1.addChildren(node4_2);
        node4_1.addChildren(node4_1_1);

        node4.addChildren(node4_1);
        //****************************************************************

        //************************INGENIERIA******************************
        Node node5 = new Node();
        node5.setDecisionType(CONTAINS);
        node5.addKeyWord("no");
        node5.addKeyWord("negativo");
        node5.addKeyWord("nel");
        node5.setLevel(7);
        node5.setResponse("Posee usted un buen razonamiento abstracto y capacidad de resolver problemas?");

        Node node5_1 = new Node();
        node5_1.setDecisionType(CONTAINS);
        node5_1.addKeyWord("si");
        node5_1.addKeyWord("afirmativo");
        node5_1.addKeyWord("en efecto");
        node5_1.setLevel(7);
        node5_1.setResponse("Siente atraccion por el desarrollo de circuitos electricos?");

            Node node5_1_1 = new Node();
            node5_1_1.setDecisionType(CONTAINS);
            node5_1_1.addKeyWord("si");
            node5_1_1.setLevel(7);
            node5_1_1.setKey("Facultad de Ingenieria, Carrera de Ingenieria Electronica");
            node5_1_1.setResponse("");

        Node node5_2 = new Node();
        node5_2.setDecisionType(CONTAINS);
        node5_2.addKeyWord("no");
        node5_2.setLevel(7);
        node5_2.setResponse("Le gustaria trabajar con desarrollo de software?");

            Node node5_2_1 = new Node();
            node5_2_1.setDecisionType(CONTAINS);
            node5_2_1.addKeyWord("si");
            node5_2_1.setLevel(7);
            node5_2_1.setKey("Facultad de Ingenieria, Carrera de Ingenieria en Sistemas");
            node5_2_1.setResponse("");

        Node node5_3 = new Node();
        node5_3.setDecisionType(CONTAINS);
        node5_3.addKeyWord("no");
        node5_3.setLevel(7);
        node5_3.setResponse("Tiene facilidad para la fisica y le gustaria optimizar porcesos?");

            Node node5_3_1 = new Node();
            node5_3_1.setDecisionType(CONTAINS);
            node5_3_1.addKeyWord("si");
            node5_3_1.setLevel(7);
            node5_3_1.setKey("Facultad de Ingenieria, Carrera de Ingenieria Industrial");
            node5_3_1.setResponse("");

        Node node5_4 = new Node();
        node5_4.setDecisionType(CONTAINS);
        node5_4.addKeyWord("no");
        node5_4.setLevel(7);
        node5_4.setResponse("Le gustaria trabajar con motores?");

            Node node5_4_1 = new Node();
            node5_4_1.setDecisionType(CONTAINS);
            node5_4_1.addKeyWord("si");
            node5_4_1.setLevel(7);
            node5_4_1.setKey("Facultad de Ingenieria, Carrera de Ingenieria Mecatronica");
            node5_4_1.setResponse("");

        node5_4.addChildren(node5_4_1);

        node5_3.addChildren(node5_4);
        node5_3.addChildren(node5_3_1);

        node5_2.addChildren(node5_3);
        node5_2.addChildren(node5_2_1);

        node5_1.addChildren(node5_2);
        node5_1.addChildren(node5_1_1);

        node5.addChildren(node5_1);
        //****************************************************************

        //***************************CIENCIAS ECONOMICAS******************
        Node node6 = new Node();
        node6.setDecisionType(CONTAINS);
        node6.addKeyWord("no");
        node6.addKeyWord("negativo");
        node6.addKeyWord("nel");
        node6.setLevel(8);
        node6.setResponse("Posee un gusto especial por el dinero y los negocios?");


        Node node6_1 = new Node();
        node6_1.setDecisionType(CONTAINS);
        node6_1.addKeyWord("si");
        node6_1.addKeyWord("afirmativo");
        node6_1.addKeyWord("en efecto");
        node6_1.setLevel(7);
        node6_1.setResponse("Siente atraccion por la publicidad y todo lo que involucra?");

            Node node6_1_1 = new Node();
            node6_1_1.setDecisionType(CONTAINS);
            node6_1_1.addKeyWord("si");
            node6_1_1.setLevel(7);
            node6_1_1.setKey("Facultad de Ciencias Economicas, Carrera de Administracion con Mercadeo");
            node6_1_1.setResponse("");

        Node node6_2 = new Node();
        node6_2.setDecisionType(CONTAINS);
        node6_2.addKeyWord("no");
        node6_2.setLevel(7);
        node6_2.setResponse("Le gustaria trabajar con sumas monetarias y graficas?");

            Node node6_2_1 = new Node();
            node6_2_1.setDecisionType(CONTAINS);
            node6_2_1.addKeyWord("si");
            node6_2_1.setLevel(7);
            node6_2_1.setKey("Facultad de Ciencias Economicas, Carrera de Administracion con Finanzas");
            node6_2_1.setResponse("");

        Node node6_3 = new Node();
        node6_3.setDecisionType(CONTAINS);
        node6_3.addKeyWord("no");
        node6_3.setLevel(7);
        node6_3.setResponse("Posee buen dominio del ingles y le gustaria tratar con otro paises en su trabajo?");

            Node node6_3_1 = new Node();
            node6_3_1.setDecisionType(CONTAINS);
            node6_3_1.addKeyWord("si");
            node6_3_1.setLevel(7);
            node6_3_1.setKey("Facultad de Ciencias Economicas, Carrera de Administracion con Negocios Internacionales");
            node6_3_1.setResponse("");

        Node node6_4 = new Node();
        node6_4.setDecisionType(CONTAINS);
        node6_4.addKeyWord("no");
        node6_4.setLevel(7);
        node6_4.setResponse("Le gustaria trabajar con administracion de hoteles?");

            Node node6_4_1 = new Node();
            node6_4_1.setDecisionType(CONTAINS);
            node6_4_1.addKeyWord("si");
            node6_4_1.setLevel(7);
            node6_4_1.setKey("Facultad de Ciencias Economicas, Carrera de Administracion de Instituciones Hoteleras");
            node6_4_1.setResponse("");

        node6_4.addChildren(node6_4_1);

        node6_3.addChildren(node5_4);
        node6_3.addChildren(node6_3_1);

        node6_2.addChildren(node6_3);
        node6_2.addChildren(node6_2_1);

        node6_1.addChildren(node6_2);
        node6_1.addChildren(node6_1_1);

        node6.addChildren(node6_1);
        //****************************************************************

        //********************ADMINISTRACION******************************
        Node node7 = new Node();
        node7.setDecisionType(CONTAINS);
        node7.addKeyWord("no");
        node7.addKeyWord("negativo");
        node7.addKeyWord("nel");
        node7.setLevel(9);
        node7.setResponse("Le gusta a usted organizar cualquier tipo de evento y posee capacidad de liderazgo?");

        Node node7_1 = new Node();
        node7_1.setDecisionType(CONTAINS);
        node7_1.addKeyWord("si");
        node7_1.addKeyWord("afirmativo");
        node7_1.addKeyWord("en efecto");
        node7_1.setLevel(7);
        node7_1.setResponse("Le gustaria administrar negocios con cedes en otros paises?");

            Node node7_1_1 = new Node();
            node7_1_1.setDecisionType(CONTAINS);
            node7_1_1.addKeyWord("si");
            node7_1_1.setLevel(7);
            node7_1_1.setKey("Facultad de Administracion de Empresas, Carrera de Administracion de Negocios Internacionales");
            node7_1_1.setResponse("");

        Node node7_2 = new Node();
        node7_2.setDecisionType(CONTAINS);
        node7_2.addKeyWord("no");
        node7_2.setLevel(7);
        node7_2.setResponse("Le gustaria administrar temas relacionados con el personal de una empresa?");

            Node node7_2_1 = new Node();
            node7_2_1.setDecisionType(CONTAINS);
            node7_2_1.addKeyWord("si");
            node7_2_1.setLevel(7);
            node7_2_1.setKey("Facultad de Administracion de Empresas, Carrera de Administracion de Recursos Humanos");
            node7_2_1.setResponse("");

        Node node7_3 = new Node();
        node7_3.setDecisionType(CONTAINS);
        node7_3.addKeyWord("no");
        node7_3.setLevel(7);
        node7_3.setResponse("Le gustaria administrar temas financieros de una empresa?");

            Node node7_3_1 = new Node();
            node7_3_1.setDecisionType(CONTAINS);
            node7_3_1.addKeyWord("si");
            node7_3_1.setLevel(7);
            node7_3_1.setKey("Facultad de Administracion de Empresas, Carrera de Administracion de Bancas y Finanzas");
            node7_3_1.setResponse("");

        Node node7_4 = new Node();
        node7_4.setDecisionType(CONTAINS);
        node7_4.addKeyWord("no");
        node7_4.setLevel(7);
        node7_4.setResponse("Le gustaria administrar temas relacionados a logistica?");

        Node node7_4_1 = new Node();
        node7_4_1.setDecisionType(CONTAINS);
        node7_4_1.addKeyWord("si");
        node7_4_1.setLevel(7);
        node7_4_1.setKey("Facultad de Administracion de Empresas, Carrera de Administracion de Logistica");
        node7_4_1.setResponse("");

        node7_4.addChildren(node7_4_1);

        node7_3.addChildren(node7_4);
        node7_3.addChildren(node7_3_1);

        node7_2.addChildren(node7_3);
        node7_2.addChildren(node7_2_1);

        node7_1.addChildren(node7_2);
        node7_1.addChildren(node7_1_1);

        node7.addChildren(node7_1);
        //****************************************************************

        node6.addChildren(node7);
        node5.addChildren(node6);
        node4.addChildren(node5);
        node3.addChildren(node4);
        node2.addChildren(node3);
        node1.addChildren(node2);
        node1.addChildren(exit);
        node1.addChildren(validationDenied);
        node1.addChildren(validationFailed);
        //****************************************************************

        //****************************************************************
        Node node98 = new Node();
        node98.setDecisionType(CONTAINS);
        node98.addKeyWord("gracias");
        node98.setLevel(2);
        node98.setResponse("Ha sido un gusto servirle");
        //****************************************************************

        //****************************************************************
        Node node99 = new Node();
        node99.setDecisionType(CONTAINS_ALL);
        node99.addKeyWord("buena");
        node99.addKeyWord("onda");
        node99.setLevel(2);
        node99.setResponse("Vivo :D");
        //****************************************************************

        //****************************************************************
        Node node100 = new Node();
        node100.setDecisionType(CONTAINS_ALL);
        node100.addKeyWord("chiste");
        node100.setLevel(2);
        node100.setResponse("Que es un terapeuta? 1024 gigapeutas (☞ﾟヮﾟ)☞");
        //****************************************************************

        //****************************************************************
        Node node103 = new Node();
        node103.setDecisionType(CONTAINS_ALL);
        node103.addKeyWord("como");
        node103.addKeyWord("estas");
        node103.setLevel(2);
        node103.setResponse("De la mejor manera, listo para ayudarlo a resover sus dudas :)");
        //****************************************************************


        masterNode.addChildren(node1);
        masterNode.addChildren(node2);
        masterNode.addChildren(node3);
        masterNode.addChildren(node98);
        masterNode.addChildren(node99);
        masterNode.addChildren(node100);
        masterNode.addChildren(node103);

        return masterNode;
    }
}
