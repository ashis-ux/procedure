const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let procedureId = null;

let currentPage = 0;

const pageSize = 10;

let deleteParameterId = null;

let deleteModal = null;


/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    procedureId = getProcedureIdFromUrl();

    initializeEvents();

    loadProcedure();
	
	deleteModal = new bootstrap.Modal(

	    document.getElementById("deleteModal")

	);

	document
	    .getElementById("confirmDeleteBtn")
	    .addEventListener("click", confirmDelete);

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("addParameterBtn")
        .addEventListener("click", addParameter);

}


/*=========================================================
    GET PROCEDURE ID
=========================================================*/

function getProcedureIdFromUrl() {

    const urlParts =
        window.location.pathname.split("/");

    return urlParts[urlParts.length - 2];

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

            loadParameters();

        })

        .catch(error => {

            showError(

                error.message ||

                "Unable to load Procedure."

            );

        });

}


/*=========================================================
    LOAD PARAMETERS
=========================================================*/

function loadParameters() {

    const url =

        BASE_URL +

        "/api/procedure/" +

        procedureId +

        "/parameters?page=" +

        currentPage +

        "&size=" +

        pageSize;

    fetch(url)

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

            populateTable(data);

            createPagination(data);

        })

        .catch(error => {

            showError(

                error.message ||

                "Unable to load Parameters."

            );

        });

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

    document.getElementById("schemaName").value =
        procedure.schemaName || "";

    document.getElementById("packageName").value =
        procedure.packageName || "";

    document.getElementById("procedureType").value =
        procedure.procedureType || "";

    document.getElementById("httpMethod").value =
        procedure.httpMethod || "";

    document.getElementById("timeoutSeconds").value =
        procedure.timeoutSeconds || "";

    document.getElementById("status").value =
        procedure.active === "Y"
            ? "Active"
            : "Non Active";

}


/*=========================================================
    TABLE
=========================================================*/

function populateTable(pageData) {

    const tbody =
        document.getElementById("parameterTableBody");

    tbody.innerHTML = "";

    if (!pageData.content ||
        pageData.content.length === 0) {

        tbody.innerHTML =

            `<tr>

                <td colspan="8"
                    class="text-center">

                    No Parameters Found

                </td>

            </tr>`;

        document.getElementById("recordCount").innerHTML =
            "Showing 0 Records";

        return;

    }

    pageData.content.forEach(parameter => {

        tbody.innerHTML +=

            `<tr>

                <td>

                    ${parameter.parameterOrder}

                </td>

                <td>

                    ${parameter.parameterName}

                </td>

                <td>

                    ${parameter.dataType}

                </td>

                <td>

                    ${parameter.parameterMode}

                </td>

                <td>

                    ${parameter.required === "Y"
                        ? "Yes"
                        : "No"}

                </td>

                <td>

                    ${parameter.defaultValue || "-"}

                </td>

                <td>

                    ${parameter.active === "Y"

                        ? '<span class="status-active">Active</span>'

                        : '<span class="status-inactive">Non Active</span>'}

                </td>

                <td>

                    <button

                        class="btn btn-primary btn-sm action-btn"

                        onclick="editParameter('${parameter.parameterId}')"

                        title="Edit">

                        <i class="bi bi-pencil-square"></i>

                    </button>

                    <button

                        class="btn btn-danger btn-sm action-btn ms-1"

                        onclick="deleteParameter('${parameter.parameterId}')"

                        title="Delete">

                        <i class="bi bi-trash"></i>

                    </button>

                </td>

            </tr>`;

    });

    document.getElementById("recordCount").innerHTML =

        "Showing " +

        (pageData.number * pageData.size + 1)

        +

        " - "

        +

        (pageData.number * pageData.size + pageData.numberOfElements)

        +

        " of "

        +

        pageData.totalElements +

        " Records";

}


/*=========================================================
    DELETE PARAMETER
=========================================================*/

function deleteParameter(parameterId) {

    deleteParameterId = parameterId;

    deleteModal.show();

}

/*=========================================================
    PAGINATION
=========================================================*/

function createPagination(pageData) {

    const pagination =
        document.getElementById("pagination");

    pagination.innerHTML = "";

    if (pageData.totalPages <= 1) {

        return;

    }

    pagination.innerHTML +=

        `<li class="page-item ${pageData.first ? 'disabled' : ''}">

            <a class="page-link"

                href="#"

                onclick="previousPage()">

                Previous

            </a>

        </li>`;

    for (let i = 0; i < pageData.totalPages; i++) {

        pagination.innerHTML +=

            `<li class="page-item ${i === pageData.number ? 'active' : ''}">

                <a

                    class="page-link"

                    href="#"

                    onclick="goToPage(${i})">

                    ${i + 1}

                </a>

            </li>`;

    }

    pagination.innerHTML +=

        `<li class="page-item ${pageData.last ? 'disabled' : ''}">

            <a

                class="page-link"

                href="#"

                onclick="nextPage(${pageData.totalPages})">

                Next

            </a>

        </li>`;

}


/*=========================================================
    PAGINATION METHODS
=========================================================*/

function goToPage(page) {

    currentPage = page;

    loadParameters();

}


function previousPage() {

    if (currentPage > 0) {

        currentPage--;

        loadParameters();

    }

}


function nextPage(totalPages) {

    if (currentPage < totalPages - 1) {

        currentPage++;

        loadParameters();

    }

}

/*=========================================================
    ADD PARAMETER
=========================================================*/

function addParameter() {

    window.location.href =

        BASE_URL +

        "/procedure/" +

        procedureId +

        "/parameter/create";

}


/*=========================================================
    EDIT PARAMETER
=========================================================*/

function editParameter(parameterId) {

    window.location.href =

        BASE_URL +

        "/procedure/parameter/update/" +

        parameterId;

}


/*=========================================================
    DELETE PARAMETER
=========================================================*/
 
function confirmDelete() {

    fetch(

        BASE_URL +

        "/api/procedure/parameter/" +

        deleteParameterId,

        {

            method: "DELETE"

        })

        .then(response => {

            if (!response.ok) {

                return response.json()

                    .then(error => {

                        throw error;

                    });

            }

            deleteModal.hide();

            showSuccess(

                "Parameter deleted successfully."

            );

            loadParameters();

        })

        .catch(error => {

            deleteModal.hide();

            showError(

                error.message ||

                "Unable to delete Parameter."

            );

        });

}
/*=========================================================
    REFRESH
=========================================================*/

function refreshParameters() {

    currentPage = 0;

    loadParameters();

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

function disableAddButton() {

    document
        .getElementById("addParameterBtn")
        .disabled = true;

}


function enableAddButton() {

    document
        .getElementById("addParameterBtn")
        .disabled = false;

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
    RESET
=========================================================*/

function refreshPage() {

    loadProcedure();

}


/*=========================================================
    FORMATTERS
=========================================================*/

function formatStatus(status) {

    return status === "Y"

        ? "Active"

        : "Non Active";

}


function formatRequired(required) {

    return required === "Y"

        ? "Yes"

        : "No";

}