const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let originalData = null;

/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    initializeEvents();

    loadDatabase();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("updateBtn")
        .addEventListener("click", updateDatabase);

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
    LOAD DATABASE
=========================================================*/

function loadDatabase() {

    clearMessages();

    const databaseId =
        document.getElementById("databaseId").value;

    fetch(BASE_URL + "/api/database/" + databaseId)

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

            originalData = data;

            populateForm(data);

        })

        .catch(error => {

            showError(
                error.message ||
                "Unable to load database.");

        });

}


/*=========================================================
    POPULATE FORM
=========================================================*/

function populateForm(data) {

    document.getElementById("databaseIdDisplay").value =
        data.databaseId;

    document.getElementById("databaseName").value =
        data.databaseName;

    document.getElementById("databaseType").value =
        data.databaseType;

    document.getElementById("host").value =
        data.host;

    document.getElementById("port").value =
        data.port;

    document.getElementById("serviceName").value =
        data.serviceName || "";

    document.getElementById("sid").value =
        data.sid || "";

    document.getElementById("username").value =
        data.username;

    document.getElementById("password").value =
        data.password;

    document.getElementById("active").value =
        data.active;

}


/*=========================================================
    RESET
=========================================================*/

function resetForm() {

    if (originalData != null) {

        populateForm(originalData);

    }

}


/*=========================================================
    PASSWORD
=========================================================*/

function togglePassword() {

    const password =
        document.getElementById("password");

    const icon =
        document.querySelector("#togglePassword i");

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
    UPDATE DATABASE
=========================================================*/

function updateDatabase() {

    clearMessages();

    if (!validateForm()) {

        return;

    }

    const databaseId =
        document.getElementById("databaseId").value;

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

    fetch(BASE_URL + "/api/database/" + databaseId, {

        method: "PUT",

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

        originalData = data;

        populateForm(data);

        showSuccess("Database updated successfully.");

    })

    .catch(error => {

        showError(
            error.message ||
            "Unable to update database.");

    });

}


/*=========================================================
    VALIDATE FORM
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

    if (!validateServiceNameOrSid()) {

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

        showError(
            "Database Name cannot exceed 100 characters.");

        return false;

    }

    return true;

}


/*=========================================================
    HOST
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

        showError(
            "Please enter a valid IP Address.");

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

    if (portNumber < 1 ||
        portNumber > 65535) {

        showError(
            "Port must be between 1 and 65535.");

        return false;

    }

    return true;

}


/*=========================================================
    SERVICE NAME / SID
=========================================================*/

function validateServiceNameOrSid() {

    const serviceName =
        getValue("serviceName").trim();

    const sid =
        getValue("sid").trim();

    if (serviceName === "" &&
        sid === "") {

        showError(
            "Please enter either Service Name or SID.");

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

        showError(
            "Username cannot exceed 100 characters.");

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

        showError(
            "Password cannot exceed 100 characters.");

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
function disableUpdateButton() {

    document.getElementById("updateBtn")
        .disabled = true;

}


function enableUpdateButton() {

    document.getElementById("updateBtn")
        .disabled = false;

}