const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let databaseList = [];

document.addEventListener("DOMContentLoaded", function () {

    initializeEvents();

    loadDatabases();

    loadProcedureTypes();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("saveBtn")
        .addEventListener("click", saveProcedure);

    document
        .getElementById("resetBtn")
        .addEventListener("click", resetForm);

    initializeTimeoutField();

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

            databaseList = data;

            populateDatabaseDropdown();

        })

        .catch(error => {

            showError(
                error.message ||
                "Unable to load databases.");

        });

}


/*=========================================================
    DATABASE DROPDOWN
=========================================================*/

function populateDatabaseDropdown() {

    const dropdown =
        document.getElementById("databaseId");

    dropdown.innerHTML =

        `<option value="">

            Select Database

        </option>`;

    databaseList.forEach(database => {

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
    RESET
=========================================================*/

function resetForm() {

    document
        .getElementById("procedureCreateForm")
        .reset();

    document.getElementById("httpMethod").value = "POST";

    document.getElementById("timeoutSeconds").value = "60";

    clearMessages();

}
/*=========================================================
    SAVE PROCEDURE
=========================================================*/

function saveProcedure() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

    disableSaveButton();

    const dto = {

        databaseId:
            parseInt(getValue("databaseId")),

        schemaName:
            getValue("schemaName"),

        packageName:
            getValue("packageName"),

        procedureName:
            getValue("procedureName"),

        description:
            getValue("description"),

        procedureType:
            getValue("procedureType"),

        httpMethod:
            "POST",

        timeoutSeconds:
            parseInt(getValue("timeoutSeconds")),

        active:
            getValue("active")

    };

    fetch(BASE_URL + "/api/procedure", {

        method: "POST",

        headers: {

            "Content-Type": "application/json"

        },

        body: JSON.stringify(dto)

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

            "Procedure created successfully.<br>" +

            "<strong>Generated Procedure ID : "
	
            + data.procedureId + " And UUid: " + data.procedureUuid+

            "</strong>"

        );

        document
            .getElementById("procedureCreateForm")
            .reset();

        document.getElementById("httpMethod").value = "POST";

        document.getElementById("timeoutSeconds").value = "60";

    })

    .catch(error => {

        enableSaveButton();

        showError(

            error.message ||

            "Unable to create procedure."

        );

    });

}


/*=========================================================
    VALIDATE FORM
=========================================================*/

function validateForm() {

    if (!validateDatabase()) {

        return false;

    }

    if (!validateSchemaName()) {

        return false;

    }

    if (!validateProcedureName()) {

        return false;

    }

    if (!validateProcedureType()) {

        return false;

    }

    if (!validateTimeout()) {

        return false;

    }

    return true;

}


/*=========================================================
    DATABASE
=========================================================*/

function validateDatabase() {

    if (getValue("databaseId") === "") {

        showError("Please select Database.");

        return false;

    }

    return true;

}


/*=========================================================
    SCHEMA NAME
=========================================================*/

function validateSchemaName() {

    const schemaName =
        getValue("schemaName").trim();

    if (schemaName === "") {

        showError("Schema Name is mandatory.");

        return false;

    }

    if (schemaName.length > 100) {

        showError(

            "Schema Name cannot exceed 100 characters."

        );

        return false;

    }

    return true;

}


/*=========================================================
    PROCEDURE NAME
=========================================================*/

function validateProcedureName() {

    const procedureName =
        getValue("procedureName").trim();

    if (procedureName === "") {

        showError("Procedure Name is mandatory.");

        return false;

    }

    if (procedureName.length > 100) {

        showError(

            "Procedure Name cannot exceed 100 characters."

        );

        return false;

    }

    return true;

}


/*=========================================================
    PROCEDURE TYPE
=========================================================*/

function validateProcedureType() {

    if (getValue("procedureType") === "") {

        showError("Please select Procedure Type.");

        return false;

    }

    return true;

}


/*=========================================================
    TIMEOUT
=========================================================*/

function validateTimeout() {

    const timeout =
        getValue("timeoutSeconds");

    if (timeout === "") {

        showError("Timeout is mandatory.");

        return false;

    }

    if (!/^\d+$/.test(timeout)) {

        showError("Timeout should contain only numbers.");

        return false;

    }

    if (parseInt(timeout) < 1) {

        showError("Timeout must be greater than zero.");

        return false;

    }

    return true;

}

/*=========================================================
    TIMEOUT INPUT
=========================================================*/

function initializeTimeoutField() {

    document.getElementById("timeoutSeconds")
        .addEventListener("input", function () {

            this.value = this.value.replace(/\D/g, "");

            if (this.value.length > 5) {

                this.value = this.value.substring(0, 5);

            }

        });

}


/*=========================================================
    SCHEMA NAME
=========================================================*/

document.getElementById("schemaName")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    PACKAGE NAME
=========================================================*/

document.getElementById("packageName")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    PROCEDURE NAME
=========================================================*/

document.getElementById("procedureName")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    DESCRIPTION
=========================================================*/

document.getElementById("description")
    .addEventListener("input", function () {

        if (this.value.length > 500) {

            this.value =
                this.value.substring(0, 500);

        }

    });


/*=========================================================
    COMMON METHODS
=========================================================*/

function getValue(id) {

    return document
        .getElementById(id)
        .value;

}


/*=========================================================
    ALERTS
=========================================================*/

function clearMessages() {

    document.getElementById("successPopup")
        .style.display = "none";

    document.getElementById("errorPopup")
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
    BUTTONS
=========================================================*/

function disableSaveButton() {

    document.getElementById("saveBtn")
        .disabled = true;

}


function enableSaveButton() {

    document.getElementById("saveBtn")
        .disabled = false;

}