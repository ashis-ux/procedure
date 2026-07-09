const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let searchTimer = null;

let currentPage = 0;

const pageSize = 10;

document.addEventListener("DOMContentLoaded", function() {

    initializeEvents();

    loadDatabases();

});


/*==========================================================
    EVENTS
==========================================================*/

function initializeEvents() {

    document
        .getElementById("searchText")
        .addEventListener("keyup", debounceSearch);

    document
        .getElementById("statusFilter")
        .addEventListener("change", function() {

            currentPage = 0;

            loadDatabases();

        });

    document
        .getElementById("clearBtn")
        .addEventListener("click", clearFilter);

}


/*==========================================================
    DEBOUNCE SEARCH
==========================================================*/

function debounceSearch() {

    clearTimeout(searchTimer);

    searchTimer = setTimeout(function() {

        currentPage = 0;

        loadDatabases();

    }, 1000);

}


/*==========================================================
    LOAD DATABASES
==========================================================*/

function loadDatabases() {

    clearMessages();

    const searchText =
        document.getElementById("searchText").value.trim();

    const status =
        document.getElementById("statusFilter").value;

    let url =
        BASE_URL +
        "/api/database/search?" +
        "searchText=" + encodeURIComponent(searchText) +
        "&status=" + encodeURIComponent(status) +
        "&page=" + currentPage +
        "&size=" + pageSize;

    fetch(url)

        .then(response => {

            if (!response.ok) {

                return response.json().then(error => {

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

            showError(error.message || "Unable to load databases.");

        });

}


/*==========================================================
    TABLE
==========================================================*/

function populateTable(pageData) {

    const tbody =
        document.getElementById("databaseTableBody");

    tbody.innerHTML = "";

    if (!pageData.content || pageData.content.length === 0) {

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

    pageData.content.forEach(database => {

        tbody.innerHTML +=

            `<tr>
			
		 

                <td>${database.databaseId}</td>

                <td>${database.databaseName}</td>

                <td>${database.databaseType}</td>

                <td>${database.host}</td>

                <td>${database.port}</td>
				 

                <td>${database.username}</td>

                <td>

                    ${database.active === "Y"

                ? '<span class="status-active">Active</span>'

                : '<span class="status-inactive">Non Active</span>'}

                </td>

                <td>

                    <button

                        class="btn btn-primary btn-sm action-btn"

                        onclick="editDatabase('${database.databaseId}')"

                        title="Edit">

                        <i class="bi bi-pencil-square"></i>

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


/*==========================================================
    PAGINATION
==========================================================*/

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

    for (let i = 0;i < pageData.totalPages;i++) {

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


/*==========================================================
    PAGINATION METHODS
==========================================================*/

function goToPage(page) {

    currentPage = page;

    loadDatabases();

}

function previousPage() {

    if (currentPage > 0) {

        currentPage--;

        loadDatabases();

    }

}

function nextPage(totalPages) {

    if (currentPage < totalPages - 1) {

        currentPage++;

        loadDatabases();

    }

}


/*==========================================================
    CLEAR
==========================================================*/

function clearFilter() {

    document.getElementById("searchText").value = "";

    document.getElementById("statusFilter").value = "";

    currentPage = 0;

    loadDatabases();

}


/*==========================================================
    EDIT
==========================================================*/

function editDatabase(id) {

    window.location.href =
        BASE_URL +
        "/database/update/" +
        id;

}


/*==========================================================
    ALERTS
==========================================================*/

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