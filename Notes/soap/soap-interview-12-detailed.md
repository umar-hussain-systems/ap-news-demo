# SOAP Mock Interview — 12 Detailed Questions & Answers

## Q1. What is SOAP and how is it different from REST?
SOAP (Simple Object Access Protocol) is a contract-based messaging protocol using XML. It relies on WSDL to define operations and data types. REST is resource-oriented, uses URLs and HTTP verbs, and typically exchanges JSON.

## Q2. What is WSDL?
WSDL is the contract that defines the SOAP service interface, including operations, request/response schemas (XSD), bindings, and endpoint URLs.

## Q3. What are the main parts of a SOAP message?
Envelope (root), Header (metadata such as auth), Body (actual payload), and Fault (error information).

## Q4. Where is the SOAP URL defined?
In the WSDL under soap:address location, which tells clients where to send requests.

## Q5. How is an operation selected?
SOAP usually uses a single endpoint. The operation is chosen based on the root XML element in the SOAP Body and sometimes the SOAPAction HTTP header.

## Q6. How do you consume SOAP in Java?
By generating client stubs using tools like wsimport or Apache CXF, which create Java interfaces and classes from the WSDL, and invoking them like normal Java methods.

## Q7. How are errors handled?
Errors are returned as SOAP Faults containing fault codes and fault messages within the SOAP response body.

## Q8. How do you secure SOAP?
Using HTTPS with Basic Auth or tokens at the transport level, and WS-Security for message-level security including signatures and encryption.

## Q9. How does Struts 2 integrate with SOAP?
Struts handles MVC. SOAP clients or endpoints reside in the service layer or as separate servlets (JAX-WS/CXF) sharing business logic.

## Q10. How do you troubleshoot SOAP 500 errors?
Check application and SOAP logs, validate endpoint and namespaces, confirm authentication, and inspect SOAP Faults for root cause.

## Q11. Why still use SOAP?
Due to legacy systems, strict contracts, enterprise standards (security, reliability), and governance requirements.

## Q12. How is versioning handled?
By introducing new WSDL versions with new namespaces and endpoints, while keeping old versions active for backward compatibility.
