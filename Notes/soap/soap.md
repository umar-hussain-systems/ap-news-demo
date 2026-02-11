# SOAP Interview Cheat Sheet (Struts 2 / Enterprise)

## 1. What is SOAP?
SOAP (Simple Object Access Protocol) is a contract-based XML messaging protocol used for service-to-service communication.  
It relies on a strict interface definition (WSDL) and structured XML messages.

**Difference from REST:**
- SOAP is contract-driven and XML-based.
- REST is resource-based and usually JSON over HTTP verbs.

---

## 2. What is WSDL?
WSDL (Web Services Description Language) is the contract of a SOAP service. It defines:
- Operations (methods)
- Input and output message structures (via XSD)
- Binding (SOAP version, protocol)
- Endpoint URL

Clients generate code from the WSDL.

---

## 3. SOAP Message Structure
A SOAP message consists of:

- **Envelope** (root)
    - **Header** (optional metadata like auth, correlation id)
    - **Body** (actual request/response)
        - **Fault** (error structure if failure occurs)

---

## 4. Where is the SOAP endpoint URL?
The SOAP service URL is defined in the WSDL inside:

```xml
<soap:address location="https://host/app/ServiceName"/>
```

## 5. How does SOAP select the operation?

SOAP usually uses a single endpoint URL.
 The operation is determined by:

- The root XML element inside the SOAP Body

- Sometimes the SOAPAction HTTP header

- Not by the URL path (unlike REST).

## 6. How do you consume SOAP in Java?

Using JAX-WS or Apache CXF:

 - Generate client stubs from WSDL (wsimport / cxf-codegen).

 - Call generated interfaces like normal Java methods.

 - Framework handles XML marshalling/unmarshalling.


## 7. How are errors handled?

SOAP uses a SOAP Fault element inside the response body.
It contains:

- faultcode

- faultstring

- optional detail

REST uses HTTP status codes + JSON error responses.

