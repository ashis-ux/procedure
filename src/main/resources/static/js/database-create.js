const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

document.addEventListener("DOMContentLoaded", function () {

    initializeEvents();

});


/*=========================================================
    INITIALIZE EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("saveBtn")
        .addEventListener("click", saveDatabase);

    document
        .getElementById("resetBtn")
        .addEventListener("click", resetForm);

    document
        .getElementById("togglePassword")
        .addEventListener("click", togglePassword);

    initializeHostField();

    initializePortField();

}


/*=========================================================
    SAVE DATABASE
=========================================================*/

function saveDatabase() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

	const dto = {

	    databaseName:
	        getValue("databaseName"),

	    databaseType:
	        "ORACLE",

	    host:
	        getValue("host"),

	    port:
	        parseInt(getValue("port")),

	    serviceName:
	        getValue("serviceName"),

	    sid:
	        getValue("sid"),

	    username:
	        getValue("username"),

	    password:
	        getValue("password"),

	    active:
	        getValue("active")

	};

    fetch(BASE_URL + "/api/database", {

        method: "POST",

        headers: {

            "Content-Type": "application/json"

        },

        body: JSON.stringify(dto)

    })

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

            showSuccess("Database created successfully with ID: "+data.databaseId);

            resetForm();

        })

        .catch(error => {

            showError(error.message || "Unable to create database.");

        });

}


/*=========================================================
    RESET
=========================================================*/

function resetForm() {

    document
        .getElementById("databaseForm")
        .reset();

    document.getElementById("databaseType").value =
        "ORACLE";

    document.getElementById("port").value =
        "1521";

    document.getElementById("active").value =
        "Y";

}


/*=========================================================
    PASSWORD
=========================================================*/

function togglePassword() {

    const password =
        document.getElementById("password");

    const icon =
        document
            .querySelector("#togglePassword i");

    if (password.type === "password") {

        password.type = "text";

        icon.classList.remove("bi-eye");

        icon.classList.add("bi-eye-slash");

    }

    else {

        password.type = "password";

        icon.classList.remove("bi-eye-slash");

        icon.classList.add("bi-eye");

    }

}

/*=========================================================
    FORM VALIDATION
=========================================================*/

function validateForm() {


    if (!validateDatabaseName()) {
        return false;
    }

    if (!validateHost()) {
        return false;
    }

    if (!validatePort()) {
        return false;
    }

    if (!validateServiceNameOrSID()) {
        return false;
    }

    if (!validateUsername()) {
        return false;
    }

    if (!validatePassword()) {
        return false;
    }

    return true;

}


/*=========================================================
    DATABASE ID
=========================================================*/

function validateDatabaseId() {

    const databaseId =
        getValue("databaseId");

    if (databaseId === "") {

        showError("Database ID is mandatory.");

        return false;

    }

    if (!/^\d+$/.test(databaseId)) {

        showError("Database ID should contain only numbers.");

        return false;

    }

    return true;

}


/*=========================================================
    DATABASE NAME
=========================================================*/

function validateDatabaseName() {

    const databaseName =
        getValue("databaseName").trim();

    if (databaseName === "") {

        showError("Database Name is mandatory.");

        return false;

    }

    if (databaseName.length > 100) {

        showError("Database Name cannot exceed 100 characters.");

        return false;

    }

    return true;

}


/*=========================================================
    HOST VALIDATION
=========================================================*/

function validateHost() {

    const host =
        getValue("host").trim();

    if (host === "") {

        showError("Host is mandatory.");

        return false;

    }

    const regex =
        /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/;

    if (!regex.test(host)) {

        showError("Please enter a valid IP Address.");

        return false;

    }

    return true;

}


/*=========================================================
    PORT
=========================================================*/

function validatePort() {

    const port =
        getValue("port");

    if (port === "") {

        showError("Port is mandatory.");

        return false;

    }

    if (!/^\d+$/.test(port)) {

        showError("Port should contain only numbers.");

        return false;

    }

    const portNumber =
        parseInt(port);

    if (portNumber < 1 || portNumber > 65535) {

        showError("Port should be between 1 and 65535.");

        return false;

    }

    return true;

}


/*=========================================================
    SERVICE NAME / SID
=========================================================*/

function validateServiceNameOrSID() {

    const serviceName =
        getValue("serviceName").trim();

    const sid =
        getValue("sid").trim();

    if (serviceName === "" && sid === "") {

        showError("Please enter either Service Name or SID.");

        return false;

    }

    return true;

}


/*=========================================================
    USERNAME
=========================================================*/

function validateUsername() {

    const username =
        getValue("username").trim();

    if (username === "") {

        showError("Username is mandatory.");

        return false;

    }

    if (username.length > 100) {

        showError("Username cannot exceed 100 characters.");

        return false;

    }

    return true;

}


/*=========================================================
    PASSWORD
=========================================================*/

function validatePassword() {

    const password =
        getValue("password").trim();

    if (password === "") {

        showError("Password is mandatory.");

        return false;

    }

    if (password.length > 100) {

        showError("Password cannot exceed 100 characters.");

        return false;

    }

    return true;

}

/*=========================================================
    HOST INPUT
=========================================================*/

function initializeHostField() {

    document.getElementById("host")
        .addEventListener("input", function () {

            this.value = this.value.replace(/[^0-9.]/g, "");

            this.value = this.value.replace(/\.{2,}/g, ".");

            if (this.value.startsWith(".")) {

                this.value = this.value.substring(1);

            }

            if (this.value.length > 15) {

                this.value = this.value.substring(0, 15);

            }

        });

}


/*=========================================================
    PORT INPUT
=========================================================*/

function initializePortField() {

    document.getElementById("port")
        .addEventListener("input", function () {

            this.value = this.value.replace(/\D/g, "");

            if (this.value.length > 5) {

                this.value = this.value.substring(0, 5);

            }

        });

}


/*=========================================================
    DATABASE ID INPUT
=========================================================*/

document.getElementById("databaseId")
    .addEventListener("input", function () {

        this.value = this.value.replace(/\D/g, "");

    });


/*=========================================================
    DATABASE NAME
=========================================================*/

document.getElementById("databaseName")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    USERNAME
=========================================================*/

document.getElementById("username")
    .addEventListener("input", function () {

        this.value = this.value.trimStart();

    });


/*=========================================================
    SERVICE NAME
=========================================================*/

document.getElementById("serviceName")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    SID
=========================================================*/

document.getElementById("sid")
    .addEventListener("input", function () {

        this.value = this.value.toUpperCase();

    });


/*=========================================================
    COMMON METHODS
=========================================================*/

function getValue(id) {

    return document
        .getElementById(id)
        .value;

}


function clearMessages() {

    document.getElementById("successPopup")
        .style.display = "none";

    document.getElementById("errorPopup")
        .style.display = "none";

}


/*=========================================================
    SUCCESS
=========================================================*/

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


/*=========================================================
    ERROR
=========================================================*/

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
    ENABLE BUTTON
=========================================================*/

function enableSaveButton() {

    document.getElementById("saveBtn")
        .disabled = false;

}


/*=========================================================
    DISABLE BUTTON
=========================================================*/

function disableSaveButton() {

    document.getElementById("saveBtn")
        .disabled = true;

}