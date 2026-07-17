const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let currentPage = 0;

let pageSize = 10;

let totalPages = 0;

let searchTimer = null;

let currentClientId = null;

let clientModal = null;

let procedureModal = null;

/*==========================================================
    Initialize
==========================================================*/

$(document).ready(function() {

    clientModal = new bootstrap.Modal(
        document.getElementById("clientModal")
    );

    procedureModal = new bootstrap.Modal(
        document.getElementById("procedureModal")
    );

    initializeEvents();

    loadClients();

});


/*==========================================================
    Events
==========================================================*/

function initializeEvents() {

    $("#searchClientName").on("keyup", function() {

        clearTimeout(searchTimer);

        searchTimer = setTimeout(function() {

            currentPage = 0;

            loadClients();

        }, 500);

    });

    $("#statusFilter").on("change", function() {

        currentPage = 0;

        loadClients();

    });

    $("#clearBtn").on("click", function() {

        clearSearch();

    });

    $("#btnCreateClient").on("click", function() {

        openCreateModal();

    });

}


/*==========================================================
    Load Clients
==========================================================*/

function loadClients() {

    const clientName = $("#searchClientName").val();

    const status = $("#statusFilter").val();

    $.ajax({

        url: BASE_URL + "/api/v1/clients",

        method: "GET",

        data: {

            clientName: clientName,

            status: status,

            page: currentPage,

            size: pageSize

        },

        success: function(response) {

            renderTable(response.content);

            renderPagination(response);

            updateRecordCount(response);

        },

        error: function(xhr) {

            showError(getErrorMessage(xhr));

        }

    });

}


/*==========================================================
    Render Table
==========================================================*/

function renderTable(clients) {

    const tbody = $("#clientTableBody");

    tbody.empty();

    if (!clients || clients.length === 0) {

        tbody.append(`
            <tr>
                <td colspan="7"
                    class="text-center text-muted">

                    No Records Found

                </td>
            </tr>
        `);

        return;

    }

    $.each(clients, function(index, client) {

        tbody.append(buildRow(client));

    });



}


/*==========================================================
    Build Row
==========================================================*/

function buildRow(client) {

    const badge =
        client.active === "Y"
            ? '<span class="status-active">Active</span>'
            : '<span class="status-inactive">Inactive</span>';

    return `

    <tr>

        <td>${client.clientId}</td>

        <td>${client.clientUuid}</td>

        <td>${client.clientName}</td>

        <td>${client.clientDescription ?? ""}</td>

        <td>${badge}</td>

        <td>${formatDate(client.updatedDate)}</td>

        <td>

            <button
                class="btn btn-warning btn-sm action-btn editBtn"
                data-id="${client.clientId}">

                <i class="bi bi-pencil-square"></i>

            </button>

            <button
                class="btn btn-success btn-sm action-btn procedureBtn"
                data-id="${client.clientId}"
                data-name="${client.clientName}">

                <i class="bi bi-shield-lock"></i>

            </button>

        </td>

    </tr>

    `;

}


/*==========================================================
    Pagination
==========================================================*/

function renderPagination(page) {

    totalPages = page.totalPages;

    currentPage = page.number;

    const pagination = $("#pagination");

    pagination.empty();

    if (totalPages <= 1) {

        return;

    }

    pagination.append(`
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link"
               href="#"
               onclick="changePage(${currentPage - 1})">
                Previous
            </a>
        </li>
    `);

    for (let i = 0;i < totalPages;i++) {

        pagination.append(`
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link"
                   href="#"
                   onclick="changePage(${i})">

                    ${i + 1}

                </a>
            </li>
        `);

    }

    pagination.append(`
        <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link"
               href="#"
               onclick="changePage(${currentPage + 1})">

                Next

            </a>
        </li>
    `);

}

function changePage(page) {

    if (page < 0 || page >= totalPages) {

        return;

    }

    currentPage = page;

    loadClients();

}


/*==========================================================
    Record Count
==========================================================*/

function updateRecordCount(page) {

    $("#recordCount").text(

        "Showing " +

        page.numberOfElements +

        " of " +

        page.totalElements +

        " Records"

    );

}


/*==========================================================
    Clear Search
==========================================================*/

function clearSearch() {

    $("#searchClientName").val("");

    $("#statusFilter").val("");

    currentPage = 0;

    loadClients();

}

/*==========================================================
    Create Client
==========================================================*/

function openCreateModal() {

    currentClientId = null;

    $("#clientModalTitle").text("Create Client");

    $("#btnSaveClient")
        .text("Save");

    $("#clientForm")[0].reset();

    $("#clientId").val("");

    $("#clientIdDiv").hide();

    $("#uuidSection").hide();

    clientModal.show();

}


/*==========================================================
    Edit Client
==========================================================*/

$(document).on(
    "click",
    ".editBtn",
    function() {

        currentClientId =
            $(this).data("id");

        openEditModal(currentClientId);

    });

function openEditModal(clientId) {

    $.ajax({

        url:
            BASE_URL +
            "/api/v1/clients/" +
            clientId,

        method: "GET",

        success: function(client) {

            $("#clientModalTitle")
                .text("Update Client");

            $("#btnSaveClient")
                .text("Update");

            $("#clientId")
                .val(client.clientId);

            $("#displayClientId")
                .val(client.clientId);

            $("#clientUuid")
                .val(client.clientUuid);

            $("#clientName")
                .val(client.clientName);

            $("#clientDescription")
                .val(client.clientDescription);

            $("#clientStatus")
                .val(client.active);

            $("#clientIdDiv")
                .show();

            $("#uuidSection")
                .show();

            clientModal.show();

        },

        error: function(xhr) {

            showError(
                getErrorMessage(xhr));

        }

    });

}


/*==========================================================
    Save / Update
==========================================================*/

$("#btnSaveClient").on(
    "click",
    function() {

        if (!validateClient()) {

            return;

        }

        if (currentClientId == null) {

            saveClient();

        } else {

            updateClient();

        }

    });


/*==========================================================
    Save Client
==========================================================*/

function saveClient() {

    $.ajax({

        url:
            BASE_URL +
            "/api/v1/clients",

        method: "POST",

        contentType:
            "application/json",

        data:
            JSON.stringify(
                buildClientRequest()),

        success: function() {

            clientModal.hide();

            showSuccess(
                "Client created successfully.");

            loadClients();

        },

        error: function(xhr) {

            showError(
                getErrorMessage(xhr));

        }

    });

}


/*==========================================================
    Update Client
==========================================================*/

function updateClient() {

    $.ajax({

        url:
            BASE_URL +
            "/api/v1/clients/" +
            currentClientId,

        method: "PUT",

        contentType:
            "application/json",

        data:
            JSON.stringify(
                buildClientRequest()),

        success: function() {

            clientModal.hide();

            showSuccess(
                "Client updated successfully.");

            loadClients();

        },

        error: function(xhr) {

            showError(
                getErrorMessage(xhr));

        }

    });

}


/*==========================================================
    Build Request
==========================================================*/

function buildClientRequest() {

    return {

        clientName:
            $("#clientName")
                .val()
                .trim(),

        clientDescription:
            $("#clientDescription")
                .val()
                .trim(),

        active:
            $("#clientStatus")
                .val(),

        clientUuid:
            $("#clientUuid")
                .val()

    };

}


/*==========================================================
    Validation
==========================================================*/

function validateClient() {

    const clientName =
        $("#clientName")
            .val()
            .trim();

    const description =
        $("#clientDescription")
            .val()
            .trim();

    if (clientName === "") {

        showError(
            "Client Name is mandatory.");

        return false;

    }

    if (clientName.length > 100) {

        showError(
            "Client Name cannot exceed 100 characters.");

        return false;

    }

    if (description.includes(" ")) {

        showError(
            "Description cannot contain spaces.");

        return false;

    }

    return true;

}


/*==========================================================
    Regenerate UUID
==========================================================*/

$("#btnRegenerateUuid").on(
    "click",
    function() {

        if (currentClientId == null) {

            return;

        }

        if (!confirm(
            "Do you want to regenerate Client UUID ?")) {

            return;

        }

        $.ajax({

            url:
                BASE_URL +
                "/api/v1/clients/" +
                currentClientId +
                "/regenerate-uuid",

            method: "PUT",

            success: function(response) {

                $("#clientUuid")
                    .val(response.clientUuid);

                showSuccess(
                    "UUID regenerated successfully.");

            },

            error: function(xhr) {

                showError(
                    getErrorMessage(xhr));

            }

        });

    });

/*==========================================================
    Assign Procedure
==========================================================*/

$(document).on(
    "click",
    ".procedureBtn",
    function() {

        currentClientId =
            $(this).data("id");

        $("#mappingClientId")
            .val(currentClientId);

        $("#mappingClientName")
            .val($(this).data("name"));

        $("#searchProcedure")
            .val("");

        loadProcedureMappings();

        procedureModal.show();

    });


/*==========================================================
    Search Procedure
==========================================================*/

$("#searchProcedure").on(
    "keyup",
    function() {

        clearTimeout(searchTimer);

        searchTimer =
            setTimeout(function() {

                filterProcedures();

            }, 300);

    });


/*==========================================================
    Load Procedure Mapping
==========================================================*/

function loadProcedureMappings() {

    $.ajax({

        url:
            BASE_URL +
            "/api/v1/client-procedure-mappings/" +
            currentClientId,

        method: "GET",

        success: function(response) {

            renderProcedureTable(response);

        },

        error: function(xhr) {

            showError(
                getErrorMessage(xhr));

        }

    });

}


/*==========================================================
    Render Procedure Table
==========================================================*/

function renderProcedureTable(procedures) {

    const tbody =
        $("#procedureMappingTableBody");

    tbody.empty();

    if (!procedures ||
        procedures.length === 0) {

        tbody.append(`

	            <tr>

	                <td colspan="5"
	                    class="text-center">

	                    No Procedures Found

	                </td>

	            </tr>

	        `);

        return;

    }

    $.each(procedures,
        function(index, procedure) {

            tbody.append(buildProcedureRow(procedure));

        });
    updateProcedureSummary();

}


/*==========================================================
    Procedure Row
==========================================================*/

function buildProcedureRow(procedure) {

    const active = procedure.active === "Y";

    return `

<tr>

    <td class="text-center">

        <input
                type="checkbox"
                class="form-check-input procedureCheck"
                value="${procedure.procedureId}"
                ${active ? "checked" : ""}>

    </td>

    <td>${procedure.schemaName}</td>

    <td>${procedure.packageName ?? ""}</td>

    <td>${procedure.procedureName}</td>

    <td class="status-cell">

        ${active
            ? '<span class="status-active">Active</span>'
            : '<span class="status-inactive">Inactive</span>'
        }

    </td>

</tr>

`;

}

$(document).on(
    "change",
    ".procedureCheck",
    function() {

        updateProcedureSummary();

    }
);

function updateProcedureSummary() {

    const total = $(".procedureCheck").length;

    const selected = $(".procedureCheck:checked").length;

    $("#procedureRecordCount").text(

        "Showing " + total + " Procedures"

    );

    $("#selectedCount").text(

        "Selected : " + selected

    );

}


/*==========================================================
    Search Procedure
==========================================================*/

function filterProcedures() {

    const value =
        $("#searchProcedure")
            .val()
            .toLowerCase();

    $("#procedureMappingTableBody tr")
        .filter(function() {

            $(this).toggle(

                $(this)
                    .text()
                    .toLowerCase()
                    .indexOf(value) > -1

            );

        });

}


/*==========================================================
    Save Mapping
==========================================================*/

$("#btnSaveMapping").on(
    "click",
    function() {

        saveProcedureMapping();

    });


function saveProcedureMapping() {

    let procedures = [];

    $("#procedureMappingTableBody tr").each(function() {

        const checkbox = $(this).find(".procedureCheck");

        if (checkbox.length === 0) {
            return;
        }

        procedures.push({

            procedureId: Number(checkbox.val()),

            active: checkbox.is(":checked") ? "Y" : "N"

        });

    });

    console.log(procedures);

    if (procedures.length === 0) {

        showProcedureError(
            "No procedures available.");

        return;
    }

    $.ajax({

        url:
            BASE_URL +
            "/api/v1/client-procedure-mappings/" +
            currentClientId,

        method: "POST",

        contentType: "application/json",

        data: JSON.stringify({

            procedures: procedures

        }),

        success: function() {

            showProcedureSuccess(
                "Procedure Mapping updated successfully.");

            loadProcedureMappings();

            loadClients();

        },

        error: function(xhr) {

            console.log(xhr);

            showProcedureError(
                getErrorMessage(xhr));

        }

    });

}
function showProcedureSuccess(message) {

    const popup = $("#procedureSuccessPopup");

    $("#procedureErrorPopup").hide();

    popup.stop(true, true);

    popup.text(message);

    popup.fadeIn();

    setTimeout(function() {

        popup.fadeOut();

    }, 3000);

}

function showProcedureError(message) {

    const popup = $("#procedureErrorPopup");

    $("#procedureSuccessPopup").hide();

    popup.stop(true, true);

    popup.text(message);

    popup.fadeIn();

    setTimeout(function() {

        popup.fadeOut();

    }, 4000);

}
/*==========================================================
    Success Message
==========================================================*/

function showSuccess(message) {

    const popup = $("#successPopup");

    popup.stop(true, true);

    popup.removeClass("alert-danger")
        .addClass("alert-success");

    popup.text(message);

    popup.fadeIn();

    setTimeout(function() {

        popup.fadeOut();

    }, 3000);

}


/*==========================================================
    Error Message
==========================================================*/

function showError(message) {

    const popup = $("#errorPopup");

    popup.stop(true, true);

    popup.removeClass("alert-success")
        .addClass("alert-danger");

    popup.text(message);

    popup.fadeIn();

    setTimeout(function() {

        popup.fadeOut();

    }, 4000);

}


/*==========================================================
    Error Message From API
==========================================================*/

function getErrorMessage(xhr) {

    if (!xhr) {

        return "Unexpected Error.";

    }

    if (xhr.responseJSON &&
        xhr.responseJSON.errorMessage) {

        return xhr.responseJSON.errorMessage;

    }

    if (xhr.responseJSON &&
        xhr.responseJSON.message) {

        return xhr.responseJSON.message;

    }

    if (xhr.responseText) {

        try {

            const json =
                JSON.parse(xhr.responseText);

            if (json.errorMessage) {

                return json.errorMessage;

            }

        }
        catch (e) {

        }

    }

    switch (xhr.status) {

        case 400:

            return "Bad Request.";

        case 401:

            return "Unauthorized.";

        case 403:

            return "Access Denied.";

        case 404:

            return "Record Not Found.";

        case 409:

            return "Duplicate Record.";

        case 500:

            return "Internal Server Error.";

        default:

            return "Something went wrong.";

    }

}


/*==========================================================
    Format Date
==========================================================*/

function formatDate(date) {

    if (!date) {

        return "";

    }

    const d = new Date(date);

    if (isNaN(d)) {

        return date;

    }

    const day =
        String(d.getDate()).padStart(2, "0");

    const month =
        String(d.getMonth() + 1).padStart(2, "0");

    const year =
        d.getFullYear();

    const hour =
        String(d.getHours()).padStart(2, "0");

    const minute =
        String(d.getMinutes()).padStart(2, "0");

    return day + "/"
        + month + "/"
        + year + " "
        + hour + ":"
        + minute;

}


/*==========================================================
    Block Spaces In Description
==========================================================*/

$("#clientDescription").on("keypress", function(e) {

    if (e.which === 32) {

        e.preventDefault();

    }

});


/*==========================================================
    Escape HTML
==========================================================*/

function escapeHtml(value) {

    if (value == null) {

        return "";

    }

    return $("<div>")
        .text(value)
        .html();

}


/*==========================================================
    Loading
==========================================================*/

function showLoading() {

    $("body").css("cursor", "wait");

}

function hideLoading() {

    $("body").css("cursor", "default");

}


/*==========================================================
    Global Ajax Loader
==========================================================*/

$(document).ajaxStart(function() {

    showLoading();

});

$(document).ajaxStop(function() {

    hideLoading();

});


/*==========================================================
    Close Modals On ESC
==========================================================*/

$(document).keyup(function(e) {

    if (e.key === "Escape") {

        clientModal.hide();

        procedureModal.hide();

    }

});


/*==========================================================
    Refresh Grid
==========================================================*/

function refreshGrid() {

    loadClients();

}