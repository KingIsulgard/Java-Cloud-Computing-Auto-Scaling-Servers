
/**
 * WorkerCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package org.webapp.services;

    /**
     *  WorkerCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class WorkerCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public WorkerCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public WorkerCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
               // No methods generated for meps other than in-out
                
           /**
            * auto generated Axis2 call back method for ditchThreads method
            * override this method for handling normal response from ditchThreads operation
            */
           public void receiveResultditchThreads(
                    org.webapp.services.WorkerStub.DitchThreadsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from ditchThreads operation
           */
            public void receiveErrorditchThreads(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWorkLoad method
            * override this method for handling normal response from getWorkLoad operation
            */
           public void receiveResultgetWorkLoad(
                    org.webapp.services.WorkerStub.GetWorkLoadResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWorkLoad operation
           */
            public void receiveErrorgetWorkLoad(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for executeJar method
            * override this method for handling normal response from executeJar operation
            */
           public void receiveResultexecuteJar(
                    org.webapp.services.WorkerStub.ExecuteJarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from executeJar operation
           */
            public void receiveErrorexecuteJar(java.lang.Exception e) {
            }
                


    }
    