# Pet Store API Test Results Report
## Executive Summary
Some issues were identified after running the designed tests that impact the API's reliability and security.
## Functional Test Results
### Issues Identified
1. **Data Validation Issues**

    - After placing an order with invalid data in `store_error_handling.feature`, POST call on `/store/order` accepts null values for required fields when placing orders. 
      Check `store_error_handling_failure.zip` for details.

2. **Updating Issues**
    - After updating a status of a pet, `pet_status_update.feature` scenarios sometimes fails when retrieven the updated field indicating a delay in the GET responses after PUT operations in `/pet` endpoint.
      Check `pet-status-update-feature-report.zip` for details.

3. **Intermittent Failures**
    - In `store_crud_operations.feature` Order deletion shows inconsistent behavior using DELETE call on `/store/order/`
    - Sometimes orders are deleted successfully, other times "order not found" error occurs. Check `store_crud_operations_failure.zip` for details.
   
4. **Pet Creation Inconsistencies**
    - Pet creation endpoints show unpredictable success rates. Check `pet_creation_failure.zip` and  `pet_creation_success.zip` for details.
      
5. **Authentication Problems**
    - Any user can authenticate regardless of credentials
    - GET, PUT, and POST operations proceed without proper authorization.
