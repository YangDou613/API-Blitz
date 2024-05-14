let selectedCollection;
let selectedCollectionName;
let selectedCollectionDescription;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/api/1.0/user/signUpIn";

} else {

    document.addEventListener("DOMContentLoaded", function() {

        const currentPagePath = window.location.pathname;

        const sidebarLinks = document.querySelectorAll('.sidebar-link');

        sidebarLinks.forEach(link => {

            const linkPath = link.getAttribute('href');

            if (linkPath === currentPagePath) {
                link.classList.add('active');
            }
        });
    });

    requestHeader = {
        "method": "GET",
        "headers": {
            "Authorization": `Bearer ${token}`
        }
    };

    apiURL = "/api/1.0/collections/get";

    // fetch('/api/1.0/collections/get?userId=1')
    fetch(apiURL, requestHeader)
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {

            const table = document.createElement("table");
            table.classList.add("table");
            table.classList.add("table-bordered");

            const thead = document.createElement("thead");
            const theadTr = document.createElement("tr");
            theadTr.insertAdjacentHTML("beforeend", "<th>#</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>Name</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>Description</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>Actions</th>");
            thead.appendChild(theadTr);

            const tbody = document.createElement("tbody");

            let number = 1;

            // const ul = document.createElement("ul");
            // ul.classList.add("collection-table");
            //
            // const tableHeaderLi = document.createElement("li");
            // tableHeaderLi.classList.add("collection-table-header");
            // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">ID</div>`);
            // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Name</div>`);
            // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">Description</div>`);
            // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader"></div>`);
            //
            // ul.appendChild(tableHeaderLi);

            data.forEach(collection => {

                const tbodyTr = document.createElement("tr");
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-1">${number}.</td>`);
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-2">${collection["collectionName"]}</td>`);
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-3">${collection["description"]}</td>`);
                tbodyTr.insertAdjacentHTML("beforeend",
                    `<td class="col col-4"><button type="button" class="btn btn-block btn-edit">Edit</button>
                        <button type="button" class="btn btn-block btn-delete">Delete</button></td>`)

                number += 1;

                tbody.appendChild(tbodyTr);

                const editButton = tbodyTr.querySelector(".btn-edit");
                const deleteButton = tbodyTr.querySelector(".btn-delete");

                tbodyTr.addEventListener('click', () => {
                    selectedCollection = collection["id"];
                    selectedCollectionName = collection["collectionName"]
                    window.location.href =
                        "/api/1.0/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + selectedCollection;
                });

                editButton.addEventListener("click", (event) => {
                    event.stopPropagation();
                    selectedCollection = collection["id"];
                    selectedCollectionName = collection["collectionName"];
                    selectedCollectionDescription = collection["description"];
                    editCollection();
                })
                deleteButton.addEventListener("click", (event) => {
                    event.stopPropagation();
                    selectedCollection = collection["id"];
                    selectedCollectionName = collection["collectionName"];
                    deleteCollection(selectedCollectionName);
                })

                // const li = document.createElement("li");
                // li.classList.add("collection-table-row");
                // li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="ID">${collection["id"]}</div>`);
                // li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Name">${collection["collectionName"]}</div>`);
                // li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="Description">${collection["description"]}</div>`);
                //
                // const buttonDiv = document.createElement("div");
                // buttonDiv.classList.add("col", "col-4", "buttonDiv");
                //
                // const editButton = document.createElement("button");
                // editButton.type = "button";
                // editButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
                // editButton.style.marginRight = "16px";
                // editButton.style.backgroundImage = "url('/edit.png')";
                // editButton.style.backgroundSize = "contain";
                // editButton.style.backgroundRepeat = "no-repeat";
                // editButton.style.backgroundPosition = "center";
                //
                // const editIcon = document.createElement("span");
                // editIcon.classList.add("glyphicon", "glyphicon-pencil");
                // editIcon.setAttribute("aria-hidden", "true");
                //
                // editButton.appendChild(editIcon);
                //
                // const deleteButton = document.createElement("button");
                // deleteButton.type = "button";
                // deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
                // deleteButton.style.marginRight = "16px";
                // deleteButton.style.backgroundImage = "url('/delete.png')";
                // deleteButton.style.backgroundSize = "contain";
                // deleteButton.style.backgroundRepeat = "no-repeat";
                // deleteButton.style.backgroundPosition = "center";
                //
                // const deleteIcon = document.createElement("span");
                // deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
                // deleteIcon.setAttribute("aria-hidden", "true");
                //
                // deleteButton.appendChild(deleteIcon);
                //
                // buttonDiv.appendChild(editButton);
                // buttonDiv.appendChild(deleteButton);
                //
                // li.appendChild(buttonDiv);
                //
                // ul.appendChild(li);
                //
                // li.addEventListener('click', () => {
                //     selectedCollection = collection["id"];
                //     selectedCollectionName = collection["collectionName"]
                //     window.location.href =
                //         "/api/1.0/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + selectedCollection;
                // });
                // editButton.addEventListener('click', (event) => {
                //     event.stopPropagation();
                //     selectedCollection = collection["id"];
                //     selectedCollectionName = collection["collectionName"];
                //     selectedCollectionDescription = collection["description"]
                //     editCollection();
                // });
                // deleteButton.addEventListener('click', (event) => {
                //     event.stopPropagation();
                //     selectedCollection = collection["id"];
                //     selectedCollectionName = collection["collectionName"];
                //     deleteCollection(selectedCollectionName);
                // });
            });
            table.appendChild(thead);
            table.appendChild(tbody);
            const collectionContainer = document.getElementById('collection-container');
            collectionContainer.insertAdjacentHTML("beforeend",
                '<button type="button" class="btn btn-block btn-add" onClick="createCollection()">+ Create</button>');
            collectionContainer.appendChild(table);
        })
        .catch(error => {
            console.error('There was an error!', error);
        });

    function createCollection() {

        document.getElementById("collection-form").reset();

        const dom = document.getElementById("edit-container");
        dom.style.display = "block";
        const overlay = document.getElementById('overlay');
        overlay.style.display = 'block'
        overlay.addEventListener("click", (event) => {
            event.preventDefault();
            dom.style.display = "none";
            overlay.style.display = "none";
        })

        const cancelButton = document.getElementById("cancel-button");
        cancelButton.addEventListener("click", (event) => {
            event.preventDefault();
            dom.style.display = "none";
            document.getElementById('overlay').style.display = 'none';
        })

        document.getElementById("collection-form").addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(this);
            fetch('/api/1.0/collections/create', {
                method: 'POST',
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Please confirm whether the entered information is correct.");
                    } else {
                        window.location.href = "/api/1.0/collections";
                        alert("Create Successfully!");
                    }
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    }

    function editCollection() {

        document.getElementById("collectionName").value = selectedCollectionName;
        document.getElementById("description").value = selectedCollectionDescription;

        const dom = document.getElementById("edit-container");
        dom.style.display = "block";
        const overlay = document.getElementById('overlay');
        overlay.style.display = 'block'
        overlay.addEventListener("click", (event) => {
            event.preventDefault();
            dom.style.display = "none";
            overlay.style.display = "none";
        })

        const cancelButton = document.getElementById("cancel-button");
        cancelButton.addEventListener("click", (event) => {
            event.preventDefault();
            dom.style.display = "none";
            document.getElementById('overlay').style.display = 'none';
        })

        document.getElementById("collection-form").addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(this);
            fetch('/api/1.0/collections/update?collectionId=' + selectedCollection, {
                method: 'POST',
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Please confirm whether the entered information is correct.");
                    } else {
                        window.location.href = "/api/1.0/collections";
                        alert("Update Successfully!");
                    }
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    }

    function deleteCollection(selectedCollectionName) {

        // fetch('/api/1.0/collections/delete?userId=1&collectionName=' + selectedCollectionName, {
        fetch('/api/1.0/collections/delete?collectionName=' + selectedCollectionName, {
            method: 'DELETE',
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    alert("Failed to delete!");
                } else {
                    window.location.href = "/api/1.0/collections";
                    alert("Delete Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    }
}
