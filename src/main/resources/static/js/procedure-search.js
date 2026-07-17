const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let searchTimer = null;

let currentPage = 0;

const pageSize = 10;

/*=========================================================
    PAGE LOAD
=========================================================*/

document.addEventListener("DOMContentLoaded", function () {

    initializeEvents();

    loadDatabaseDropdown();

    loadProcedures();

});


/*=========================================================
    EVENTS
=========================================================*/

function initializeEvents() {

    document
        .getElementById("searchText")
        .addEventListener("keyup", debounceSearch);

    document
        .getElementById("databaseFilter")
        .addEventListener("change", function () {

            currentPage = 0;

            loadProcedures();

        });

    document
        .getElementById("statusFilter")
        .addEventListener("change", function () {

            currentPage = 0;

            loadProcedures();

        });

    document
        .getElementById("clearBtn")
        .addEventListener("click", clearFilters);

}


/*=========================================================
    DEBOUNCE SEARCH
=========================================================*/

function debounceSearch() {

    clearTimeout(searchTimer);

    searchTimer = setTimeout(function () {

        currentPage = 0;

        loadProcedures();

    }, 1000);

}


/*=========================================================
    LOAD DATABASE DROPDOWN
=========================================================*/

function loadDatabaseDropdown() {

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
        document.getElementById("databaseFilter");

    dropdown.innerHTML =

        `<option value="">

            All Databases

        </option>`;

    databases.forEach(database => {

        dropdown.innerHTML +=

            `<option value="${database.databaseId}">

                ${database.databaseName}

            </option>`;

    });

}


/*=========================================================
    LOAD PROCEDURES
=========================================================*/

function loadProcedures() {

    clearMessages();

    const searchText =
        document.getElementById("searchText").value.trim();

    const databaseId =
        document.getElementById("databaseFilter").value;

    const status =
        document.getElementById("statusFilter").value;

    const url =
        BASE_URL +
        "/api/procedure/search?" +

        "searchText=" +
        encodeURIComponent(searchText) +

        "&databaseId=" +
        encodeURIComponent(databaseId) +

        "&status=" +
        encodeURIComponent(status) +

        "&page=" +
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

                "Unable to load procedures."

            );

        });

}

/*=========================================================
    TABLE
=========================================================*/

function populateTable(pageData) {

    const tbody =
        document.getElementById("procedureTableBody");

    tbody.innerHTML = "";

    if (!pageData.content ||
        pageData.content.length === 0) {

        tbody.innerHTML =

            `<tr>

                <td colspan="8"
                    class="text-center">

                    No Records Found

                </td>

            </tr>`;

        document.getElementById("recordCount").innerHTML =
            "Showing 0 Records";

        return;

    }

    pageData.content.forEach(procedure => {

        tbody.innerHTML +=

            `<tr>

                <td>

                    ${procedure.procedureId}

                </td>

                <td>

                    ${procedure.procedureName}

                </td>

                <td>

                    ${procedure.databaseName}

                </td>

                <td>

                    ${procedure.schemaName}

                </td>

                <td>

                    ${procedure.procedureType}

                </td>

                <td>

                    ${procedure.timeoutSeconds}

                </td>

				<td class="text-center align-middle">

				    ${procedure.active === "Y"

				        ? '<span class="badge rounded-pill bg-success status-badge">Active</span>'

				        : '<span class="badge rounded-pill bg-danger status-badge">Non Active</span>'}

				</td>

				<td>

				    <!-- Edit -->

				    <button
				        class="btn btn-primary btn-sm action-btn"

				        onclick="editProcedure('${procedure.procedureId}')"

				        title="Edit Procedure">

				        <i class="bi bi-pencil-square"></i>

				    </button>

				    <!-- Parameters -->

				    <button
				        class="btn btn-success btn-sm action-btn ms-1"

				        onclick="viewParameters('${procedure.procedureId}')"

				        title="Procedure Parameters">

				        <i class="bi bi-list-ul"></i>

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

            <a class="page-link"

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

    loadProcedures();

}


function previousPage() {

    if (currentPage > 0) {

        currentPage--;

        loadProcedures();

    }

}


function nextPage(totalPages) {

    if (currentPage < totalPages - 1) {

        currentPage++;

        loadProcedures();

    }

}


/*=========================================================
    EDIT
=========================================================*/

function editProcedure(procedureId) {

    window.location.href =
        BASE_URL +
        "/procedure/update/" +
        procedureId;

}

/*=========================================================
    CLEAR FILTERS
=========================================================*/

function clearFilters() {

    document.getElementById("searchText").value = "";

    document.getElementById("databaseFilter").value = "";

    document.getElementById("statusFilter").value = "";

    currentPage = 0;

    loadProcedures();

}


/*=========================================================
    ALERTS
=========================================================*/

function clearMessages() {

    document.getElementById("successPopup").style.display = "none";

    document.getElementById("errorPopup").style.display = "none";

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

function disableClearButton() {

    document.getElementById("clearBtn").disabled = true;

}


function enableClearButton() {

    document.getElementById("clearBtn").disabled = false;

}


/*=========================================================
    COMMON METHODS
=========================================================*/

function getValue(id) {

    return document
        .getElementById(id)
        .value;

}

/*=========================================================
    VIEW PARAMETERS
=========================================================*/

function viewParameters(procedureId) {

    window.location.href =
        BASE_URL +
        "/procedure/" +
        procedureId +
        "/parameters";

}