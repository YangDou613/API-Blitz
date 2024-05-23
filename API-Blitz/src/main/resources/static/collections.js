let selectedCollection;
let selectedCollectionName;
let selectedCollectionDescription;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/signUpIn";

} else {

    document.addEventListener("DOMContentLoaded", function () {

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

    apiURL = "/api/1.0/collections";

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

            data.forEach(collection => {

                const tbodyTr = document.createElement("tr");
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-1">${number}.</td>`);
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-2">${collection["collectionName"]}</td>`);
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-3">${collection["description"]}</td>`);
                tbodyTr.insertAdjacentHTML("beforeend",
                    `<td class="col col-4"><button type="button" class="btn btn-block btn-edit">Edit</button>
                        <button type="button" class="btn btn-block btn-delete">Delete</button></td>`);

                number += 1;

                tbody.appendChild(tbodyTr);

                const editButton = tbodyTr.querySelector(".btn-edit");
                const deleteButton = tbodyTr.querySelector(".btn-delete");

                tbodyTr.addEventListener('click', () => {
                    selectedCollection = collection["id"];
                    selectedCollectionName = collection["collectionName"]
                    window.location.href =
                        "/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + selectedCollection;
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
                        window.location.href = "/collections";
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
                        window.location.href = "/collections";
                        alert("Update Successfully!");
                    }
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    }

    function deleteCollection(selectedCollectionName) {

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
                    window.location.href = "/collections";
                    alert("Delete Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    }
}
