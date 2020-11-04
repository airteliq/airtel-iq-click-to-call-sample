<h1>Airtel IQ Callflow Components APIs  </h1>
This repository contains a sample Spring Boot example project for integrating with Airtel IQ call flow components API. 
The application demonstrates a sample code for integrating the below mentioned call flow using Airtel IQ APIs 
<h2>The flow:</h2>
![Image of flow](https://assets.airtel.in/static-assets/airtel-ccp/img/click-to-call-wo-early-media.png)

<h2>Sequence diagram</h2>
![Image of sequence](https://assets.airtel.in/static-assets/airtel-ccp/img/Click_to_call_integration_steps.png)



Step 1: The integrator needs to connect with Airtel IQ to get the call back URLs whitelisted so that real time events can be received on the URL. 

Step 2: Integrator needs to call initiate call API to start the call. Airtel IQ will respond with unique vmSessionId.  
Step 3: Listen to real time events to take further action. 
Step 4: Hit subsequent APIs and listen to corresponding events to execute the complete call flow. 
Step 5: Integrator needs to develop CDR API to listen to CDR and run analytics/reporting on his end. 

 

Reference - 

https://www.airtel.in/business/b2b/airtel-iq/api-docs/callflow-component-apis 
