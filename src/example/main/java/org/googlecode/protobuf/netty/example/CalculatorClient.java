/*
 * Copyright (c) 2009 Stephen Tu <stephen_tu@berkeley.edu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.googlecode.protobuf.netty.example;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.googlecode.protobuf.netty.example.Calculator.CalcRequest;
import org.googlecode.protobuf.netty.example.Calculator.CalcResponse;
import org.googlecode.protobuf.netty.example.Calculator.CalcService;
import org.googlecode.protobuf.netty.example.Calculator.CalcService.BlockingInterface;
import org.googlecode.protobuf.netty.example.Calculator.CalcService.Stub;
import org.robotninjas.protobuf.netty.client.ClientController;
import org.robotninjas.protobuf.netty.client.NettyRpcChannel;
import org.robotninjas.protobuf.netty.client.RpcClient;

import java.net.InetSocketAddress;

public class CalculatorClient {

  public static void main(String[] args) throws InterruptedException {

    NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
    EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(1);
    RpcClient client = new RpcClient(eventLoopGroup, eventExecutorGroup);

    NettyRpcChannel channel = client.connect(new InetSocketAddress("localhost", 8080));

    Stub calcService = CalcService.newStub(channel);

    BlockingInterface blockingCalcService = CalcService.newBlockingStub(channel);

    // Get a new RpcController to use for this rpc call
    final RpcController controller = new ClientController(channel);

    // Create the request
    CalcRequest request = CalcRequest.newBuilder().setOp1(15).setOp2(35).build();

    for (; ; ) {
      // Make the (asynchronous) RPC request
      calcService.add(controller, request, new RpcCallback<CalcResponse>() {
        public void run(CalcResponse response) {
          if (response != null) {
            System.out.println("The answer is: " + response.getResult());
          } else {
            System.out.println("Oops, there was an error: " + controller.errorText());
          }
        }
      });
      Thread.sleep(150);
      controller.reset();
    }

//    // Do other important things now, while your RPC is hard at work
//    try {
//      Thread.sleep(1000L);
//    } catch (InterruptedException e) {
//      // Ignore
//    }
//
//    // Reset the controller
//    controller.reset();
//
//    // Now try a blocking RPC request
//    try {
//      CalcResponse response = blockingCalcService.multiply(controller, request);
//      if (response != null) {
//        System.out.println("The answer is: " + response.getResult());
//      } else {
//        System.out.println("Oops, there was an error: " + controller.errorText());
//      }
//    } catch (ServiceException e) {
//      e.printStackTrace();
//    }
//
//    controller.reset();
//
//    // Make a bad request
//    CalcRequest badRequest = CalcRequest.newBuilder().setOp1(20).setOp2(0).build();
//
//    // Let's see if we can trigger the exception
//    try {
//      blockingCalcService.divide(controller, badRequest);
//      System.out.println("Should not be here");
//    } catch (ServiceException e) {
//      System.out.println("Good! Error is: " + e.getMessage());
//    }
//
//    controller.reset();
//
//    // Asynchronous error
//    calcService.divide(controller, badRequest, new RpcCallback<CalcResponse>() {
//      public void run(CalcResponse response) {
//        if (response != null) {
//          System.out.println("Shouldn't happen");
//        } else {
//          System.out.println("Good! Error is: " + controller.errorText());
//        }
//      }
//    });
//
//
//    try {
//      Thread.sleep(1000L);
//    } catch (InterruptedException e) {
//      // Ignore
//    }
//
//    // Close the channel
//    channel.close();
//
//
//    System.out.println("Done!");
  }

}
