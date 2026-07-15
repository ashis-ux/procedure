const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let parameterId = null;

let procedureId = null;


/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    parameterId = getParameterIdFromUrl();

    initializeEvents();
	
	initializeInputRestrictions();

    loadDataTypes();

    loadParameterModes();

    loadParameter();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("updateBtn")
        .addEventListener("click", updateParameter);

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
    GET PARAMETER ID
=========================================================*/

function getParameterIdFromUrl() {

    const urlParts =
        window.location.pathname.split("/");

    return urlParts[urlParts.length - 1];

}


/*=========================================================
    LOAD PARAMETER
=========================================================*/

function loadParameter() {

    clearMessages();

    fetch(

        BASE_URL +

        "/api/procedure/parameter/" +

        parameterId

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

            populateParameter(data);

            procedureId = data.procedureId;

            document
                .getElementById("procedureId")
                .value = procedureId;

            document
                .getElementById("parameterId")
                .value = data.parameterId;

            loadProcedure();

        })

        .catch(error => {

            showError(

                error.message ||

                "Unable to load Parameter."

            );

        });

}


/*=========================================================
    LOAD PROCEDURE
=========================================================*/

function loadProcedure() {

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

         <option value="INOUT">INOUT</option>
		  <option value="REF_CURSOR">REF_CURSOR</option>`;

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
    POPULATE PARAMETER
=========================================================*/

function populateParameter(parameter) {

    document.getElementById("parameterName").value =
        parameter.parameterName || "";

    document.getElementById("parameterOrder").value =
        parameter.parameterOrder || "";

    document.getElementById("dataType").value =
        parameter.dataType || "";

    document.getElementById("parameterMode").value =
        parameter.parameterMode || "";

    document.getElementById("required").value =
        parameter.required || "";

    document.getElementById("defaultValue").value =
        parameter.defaultValue || "";

    document.getElementById("active").value =
        parameter.active || "Y";

}


/*=========================================================
    UPDATE PARAMETER
=========================================================*/

function updateParameter() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

    disableUpdateButton();

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

        "/api/procedure/parameter/" +

        parameterId,

        {

            method: "PUT",

            headers: {

                "Content-Type":
                    "application/json"

            },

            body: JSON.stringify(request)

        })

        .then(response => {

            enableUpdateButton();

            if (!response.ok) {

                return response.json()

                    .then(error => {

                        throw error;

                    });

            }

            return response.json();

        })

        .then(() => {

            showSuccess(

                "Parameter updated successfully."

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

            enableUpdateButton();

            showError(

                error.message ||

                "Unable to update Parameter."

            );

        });

}


/*=========================================================
    VALIDATION
=========================================================*/

function validateForm() {

    if (document.getElementById("parameterName")
        .value.trim() === "") {

        showError("Parameter Name is mandatory.");

        return false;

    }

    if (document.getElementById("parameterOrder")
        .value === "") {

        showError("Parameter Order is mandatory.");

        return false;

    }

    if (parseInt(
        document.getElementById("parameterOrder").value) <= 0) {

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

    loadParameter();

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

function initializeInputRestrictions() {

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

}


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

function disableUpdateButton() {

    document
        .getElementById("updateBtn")
        .disabled = true;

}


function enableUpdateButton() {

    document
        .getElementById("updateBtn")
        .disabled = false;

}


/*=========================================================
    PAGE REFRESH
=========================================================*/

function refreshPage() {

    loadParameter();

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
    DEBUG
=========================================================*/

function logRequest(request) {

    console.log(

        "Parameter Update Request :",

        request

    );

}