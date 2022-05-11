package org.molw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Connection connection;
        Channel channel;
        String exchangeName = "value-changed";
        String queueName = "notify-queue";
        //Todo connect using env variables
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
            channel.basicPublish(exchangeName, "", null, "hello world".getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        try{
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) ->
            {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("here is the Message: " + message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {} );

            //If I leave this in the connection closes before the callback can do its thing
            //channel.close();
            //connection.close();

        } catch (Exception e){
            System.out.println(e);
        }


        // Make an exchange with no queues
        System.out.println( "Done" );
    }
}
