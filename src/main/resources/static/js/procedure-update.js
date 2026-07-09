const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let procedureId = null;

/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    procedureId = getProcedureIdFromUrl();

    initializeEvents();

    loadDatabases();

    loadProcedureTypes();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("updateBtn")
        .addEventListener("click", updateProcedure);

    document
        .getElementById("resetBtn")
        .addEventListener("click", loadProcedure);

}


/*=========================================================
    PROCEDURE ID
=========================================================*/

function getProcedureIdFromUrl() {

    const urlParts =
        window.location.pathname.split("/");

    return urlParts[urlParts.length - 1];

}


/*=========================================================
    LOAD DATABASES
=========================================================*/

function loadDatabases() {

    fetch(BASE_URL + "/api/database/dropdown")

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

            populateDatabaseDropdown(data);

            loadProcedure();

        })

        .catch(error => {

            showError(

                error.message ||

                "Unable to load databases."

            );

        });

}


/*=========================================================
    DATABASE DROPDOWN
=========================================================*/

function populateDatabaseDropdown(databases) {

    const dropdown =
        document.getElementById("databaseId");

    dropdown.innerHTML =

        `<option value="">Select Database</option>`;

    databases.forEach(database => {

        dropdown.innerHTML +=

            `<option value="${database.databaseId}">

                ${database.databaseName}

            </option>`;

    });

}


/*=========================================================
    PROCEDURE TYPE
=========================================================*/

function loadProcedureTypes() {

    const dropdown =
        document.getElementById("procedureType");

    dropdown.innerHTML = `

        <option value="">Select</option>

        <option value="NON_CURSOR">

            NON_CURSOR

        </option>

        <option value="CURSOR">

            CURSOR

        </option>

        <option value="CURSOR_OUT">

            CURSOR_OUT

        </option>

        <option value="OUT">

            OUT

        </option>

        <option value="MULTI_CURSOR">

            MULTI_CURSOR

        </option>

    `;

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

        populateForm(data);

    })

    .catch(error => {

        showError(

            error.message ||

            "Unable to load Procedure."

        );

    });

}
/*=========================================================
    POPULATE FORM
=========================================================*/

function populateForm(procedure) {
	
	document.getElementById("procedureUuid").value =
	    procedure.procedureUuid;

    document.getElementById("databaseId").value =
        procedure.databaseId;

    document.getElementById("schemaName").value =
        procedure.schemaName || "";

    document.getElementById("packageName").value =
        procedure.packageName || "";

    document.getElementById("procedureName").value =
        procedure.procedureName || "";

    document.getElementById("description").value =
        procedure.description || "";

    document.getElementById("procedureType").value =
        procedure.procedureType;

    document.getElementById("httpMethod").value =
        procedure.httpMethod;

    document.getElementById("timeoutSeconds").value =
        procedure.timeoutSeconds;

    document.getElementById("active").value =
        procedure.active;

}


/*=========================================================
    UPDATE PROCEDURE
=========================================================*/

function updateProcedure() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

    disableUpdateButton();

    const dto = {

        databaseId:
            parseInt(document.getElementById("databaseId").value),

        schemaName:
            document.getElementById("schemaName").value.trim(),

        packageName:
            document.getElementById("packageName").value.trim(),

        procedureName:
            document.getElementById("procedureName").value.trim(),

        description:
            document.getElementById("description").value.trim(),

        procedureType:
            document.getElementById("procedureType").value,

        timeoutSeconds:
            parseInt(document.getElementById("timeoutSeconds").value),

        active:
            document.getElementById("active").value

    };

    fetch(

        BASE_URL +

        "/api/procedure/" +

        procedureId,

        {

            method: "PUT",

            headers: {

                "Content-Type": "application/json"

            },

            body: JSON.stringify(dto)

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

        .then(data => {

            showSuccess(

                "Procedure updated successfully."

            );

        })

        .catch(error => {

            enableUpdateButton();

            showError(

                error.message ||

                "Unable to update Procedure."

            );

        });

}


/*=========================================================
    VALIDATION
=========================================================*/

function validateForm() {

    if (document.getElementById("databaseId").value === "") {

        showError("Please select Database.");

        return false;

    }

    if (document.getElementById("schemaName").value.trim() === "") {

        showError("Schema Name is mandatory.");

        return false;

    }

    if (document.getElementById("procedureName").value.trim() === "") {

        showError("Procedure Name is mandatory.");

        return false;

    }

    if (document.getElementById("procedureType").value === "") {

        showError("Please select Procedure Type.");

        return false;

    }

    const timeout =

        document.getElementById("timeoutSeconds").value;

    if (timeout === "") {

        showError("Timeout is mandatory.");

        return false;

    }

    if (!/^[0-9]+$/.test(timeout)) {

        showError("Timeout should contain only numbers.");

        return false;

    }

    if (parseInt(timeout) <= 0) {

        showError("Timeout should be greater than zero.");

        return false;

    }

    return true;

}

/*=========================================================
    RESET
=========================================================*/

function resetForm() {

    loadProcedure();

    clearMessages();

}


/*=========================================================
    INPUT RESTRICTIONS
=========================================================*/

function initializeInputRestrictions() {

    /*
     * Schema Name
     */
    document
        .getElementById("schemaName")
        .addEventListener("input", function () {

            this.value = this.value.toUpperCase();

        });

    /*
     * Package Name
     */
    document
        .getElementById("packageName")
        .addEventListener("input", function () {

            this.value = this.value.toUpperCase();

        });

    /*
     * Procedure Name
     */
    document
        .getElementById("procedureName")
        .addEventListener("input", function () {

            this.value = this.value.toUpperCase();

        });

    /*
     * Description
     */
    document
        .getElementById("description")
        .addEventListener("input", function () {

            if (this.value.length > 500) {

                this.value =
                    this.value.substring(0, 500);

            }

        });

    /*
     * Timeout
     */
    document
        .getElementById("timeoutSeconds")
        .addEventListener("input", function () {

            this.value =
                this.value.replace(/\D/g, "");

            if (this.value.length > 5) {

                this.value =
                    this.value.substring(0, 5);

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
    OPTIONAL HELPERS
=========================================================*/

function clearForm() {

    document
        .getElementById("procedureUpdateForm")
        .reset();

}


function isEmpty(value) {

    return value == null ||

           value.trim() === "";

}	