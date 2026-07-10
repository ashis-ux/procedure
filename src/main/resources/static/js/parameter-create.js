const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let procedureId = null;


/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    procedureId = getProcedureIdFromUrl();

    initializeEvents();

    loadDataTypes();

    loadParameterModes();

    loadProcedure();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("saveBtn")
        .addEventListener("click", saveParameter);

    document
        .getElementById("resetBtn")
        .addEventListener("click", resetForm);

    document
        .getElementById("cancelBtn")
        .addEventListener("click", goBack);

    document
        .getElementById("backBtn")
        .addEventListener("click", goBack);

}


/*=========================================================
    GET PROCEDURE ID
=========================================================*/

function getProcedureIdFromUrl() {

    const urlParts =
        window.location.pathname.split("/");

    return urlParts[urlParts.length - 3];

}


/*=========================================================
    LOAD PROCEDURE
=========================================================*/

function loadProcedure() {

    clearMessages();

    fetch(

        BASE_URL +

        "/api/procedure/" +

        procedureId

    )

        .then(response => {

            if (!response.ok) {

                return response.json()

                    .then(error => {

                        throw error;

                    });

            }

            return response.json();

        })

        .then(data => {

            populateProcedure(data);

        })

        .catch(error => {

            showError(

                error.message ||

                "Unable to load Procedure."

            );

        });

}


/*=========================================================
    LOAD DATA TYPES
=========================================================*/

function loadDataTypes() {

    const dropdown =
        document.getElementById("dataType");

    dropdown.innerHTML =

        `<option value="">Select Data Type</option>

         <option value="VARCHAR2">VARCHAR2</option>

         <option value="CHAR">CHAR</option>

         <option value="NUMBER">NUMBER</option>

         <option value="DATE">DATE</option>

         <option value="TIMESTAMP">TIMESTAMP</option>

         <option value="CLOB">CLOB</option>

         <option value="BLOB">BLOB</option>

         <option value="REF_CURSOR">REF_CURSOR</option>`;

}


/*=========================================================
    LOAD PARAMETER MODES
=========================================================*/

function loadParameterModes() {

    const dropdown =
        document.getElementById("parameterMode");

    dropdown.innerHTML =

        `<option value="">Select Mode</option>

         <option value="IN">IN</option>

         <option value="OUT">OUT</option>

         <option value="INOUT">INOUT</option>`;

}

/*=========================================================
    POPULATE PROCEDURE
=========================================================*/

function populateProcedure(procedure) {

    document.getElementById("procedureUuid").value =
        procedure.procedureUuid || "";

    document.getElementById("procedureName").value =
        procedure.procedureName || "";

    document.getElementById("databaseName").value =
        procedure.databaseName || "";

    document.getElementById("procedureType").value =
        procedure.procedureType || "";

}


/*=========================================================
    SAVE PARAMETER
=========================================================*/

function saveParameter() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

    disableSaveButton();

    const request = {

        parameterName:
            document.getElementById("parameterName")
                .value
                .trim(),

        parameterOrder:
            parseInt(
                document.getElementById("parameterOrder")
                    .value),

        dataType:
            document.getElementById("dataType")
                .value,

        parameterMode:
            document.getElementById("parameterMode")
                .value,

        required:
            document.getElementById("required")
                .value,

        defaultValue:
            document.getElementById("defaultValue")
                .value
                .trim(),

        active:
            document.getElementById("active")
                .value

    };

    fetch(

        BASE_URL +

        "/api/procedure/" +

        procedureId +

        "/parameters",

        {

            method: "POST",

            headers: {

                "Content-Type":
                    "application/json"

            },

            body: JSON.stringify(request)

        })

        .then(response => {

            enableSaveButton();

            if (!response.ok) {

                return response.json()

                    .then(error => {

                        throw error;

                    });

            }

            return response.json();

        })

        .then(data => {

            showSuccess(

                "Parameter created successfully."

            );

            setTimeout(function () {

                window.location.href =

                    BASE_URL +

                    "/procedure/" +

                    procedureId +

                    "/parameters";

            }, 1200);

        })

        .catch(error => {

            enableSaveButton();

            showError(

                error.message ||

                "Unable to create Parameter."

            );

        });

}


/*=========================================================
    VALIDATION
=========================================================*/

function validateForm() {

    if (document.getElementById("parameterName")
        .value
        .trim() === "") {

        showError("Parameter Name is mandatory.");

        return false;

    }

    if (document.getElementById("parameterOrder")
        .value === "") {

        showError("Parameter Order is mandatory.");

        return false;

    }

    if (parseInt(

        document.getElementById("parameterOrder")
            .value) <= 0) {

        showError(

            "Parameter Order should be greater than zero.");

        return false;

    }

    if (document.getElementById("dataType")
        .value === "") {

        showError("Please select Data Type.");

        return false;

    }

    if (document.getElementById("parameterMode")
        .value === "") {

        showError("Please select Parameter Mode.");

        return false;

    }

    if (document.getElementById("required")
        .value === "") {

        showError("Please select Required.");

        return false;

    }

    if (document.getElementById("active")
        .value === "") {

        showError("Please select Status.");

        return false;

    }

    return true;

}

/*=========================================================
    RESET FORM
=========================================================*/

function resetForm() {

    document.getElementById("parameterName").value = "";

    document.getElementById("parameterOrder").value = "";

    document.getElementById("dataType").selectedIndex = 0;

    document.getElementById("parameterMode").selectedIndex = 0;

    document.getElementById("required").selectedIndex = 0;

    document.getElementById("defaultValue").value = "";

    document.getElementById("active").value = "Y";

    clearMessages();

}


/*=========================================================
    GO BACK
=========================================================*/

function goBack() {

    window.location.href =

        BASE_URL +

        "/procedure/" +

        procedureId +

        "/parameters";

}


/*=========================================================
    INPUT RESTRICTIONS
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    /*
     * Parameter Name
     */
    document
        .getElementById("parameterName")
        .addEventListener("input", function () {

            this.value = this.value
                .toUpperCase()
                .replace(/[^A-Z0-9_]/g, "");

        });

    /*
     * Parameter Order
     */
    document
        .getElementById("parameterOrder")
        .addEventListener("input", function () {

            this.value = this.value
                .replace(/\D/g, "");

        });

    /*
     * Default Value Length
     */
    document
        .getElementById("defaultValue")
        .addEventListener("input", function () {

            if (this.value.length > 200) {

                this.value = this.value.substring(0, 200);

            }

        });

});


/*=========================================================
    COMMON METHODS
=========================================================*/

function getValue(id) {

    return document
        .getElementById(id)
        .value
        .trim();

}


function setValue(id, value) {

    document
        .getElementById(id)
        .value = value;

}


function isEmpty(value) {

    return value == null ||

           value.trim() === "";

}
/*=========================================================
    ALERTS
=========================================================*/

function clearMessages() {

    document
        .getElementById("successPopup")
        .style.display = "none";

    document
        .getElementById("errorPopup")
        .style.display = "none";

}


function showSuccess(message) {

    const popup =
        document.getElementById("successPopup");

    popup.innerHTML = message;

    popup.style.display = "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

}


function showError(message) {

    const popup =
        document.getElementById("errorPopup");

    popup.innerHTML = message;

    popup.style.display = "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

}


/*=========================================================
    BUTTON STATE
=========================================================*/

function disableSaveButton() {

    document
        .getElementById("saveBtn")
        .disabled = true;

}


function enableSaveButton() {

    document
        .getElementById("saveBtn")
        .disabled = false;

}


/*=========================================================
    FORMATTERS
=========================================================*/

function formatRequired(value) {

    return value === "Y"

        ? "Yes"

        : "No";

}


function formatStatus(value) {

    return value === "Y"

        ? "Active"

        : "Inactive";

}


/*=========================================================
    PAGE REFRESH
=========================================================*/

function refreshPage() {

    loadProcedure();

    resetForm();

}


/*=========================================================
    DEBUG
=========================================================*/

function logRequest(request) {

    console.log(

        "Parameter Create Request :",

        request

    );

}