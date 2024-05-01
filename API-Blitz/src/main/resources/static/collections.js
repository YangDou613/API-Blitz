let selectedCollection;
let selectedCollectionName;
let selectedCollectionDescription;

fetch('/api/1.0/collections/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {

        const ul = document.createElement("ul");
        ul.classList.add("collection-table");

        const tableHeaderLi = document.createElement("li");
        tableHeaderLi.classList.add("collection-table-header");
        tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">ID</div>`);
        tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Name</div>`);
        tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">Description</div>`);
        tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader"></div>`);

        ul.appendChild(tableHeaderLi);

        data.forEach(collection => {

            const li = document.createElement("li");
            li.classList.add("collection-table-row");
            li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="ID">${collection["id"]}</div>`);
            li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Name">${collection["collectionName"]}</div>`);
            li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="Description">${collection["description"]}</div>`);

            const buttonDiv = document.createElement("div");
            buttonDiv.classList.add("col", "col-4", "buttonDiv");

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
            editButton.style.marginRight = "16px";
            editButton.style.backgroundImage = "url('/edit.png')";
            editButton.style.backgroundSize = "contain";
            editButton.style.backgroundRepeat = "no-repeat";
            editButton.style.backgroundPosition = "center";

            const editIcon = document.createElement("span");
            editIcon.classList.add("glyphicon", "glyphicon-pencil");
            editIcon.setAttribute("aria-hidden", "true");

            editButton.appendChild(editIcon);

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
            deleteButton.style.marginRight = "16px";
            deleteButton.style.backgroundImage = "url('/delete.png')";
            deleteButton.style.backgroundSize = "contain";
            deleteButton.style.backgroundRepeat = "no-repeat";
            deleteButton.style.backgroundPosition = "center";

            const deleteIcon = document.createElement("span");
            deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
            deleteIcon.setAttribute("aria-hidden", "true");

            deleteButton.appendChild(deleteIcon);

            buttonDiv.appendChild(editButton);
            buttonDiv.appendChild(deleteButton);

            li.appendChild(buttonDiv);

            ul.appendChild(li);

            li.addEventListener('click', () => {
                selectedCollection = collection["id"];
                selectedCollectionName = collection["collectionName"]
                window.location.href =
                    "/api/1.0/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + selectedCollection;
            });
            editButton.addEventListener('click', (event) => {
                event.stopPropagation();
                selectedCollection = collection["id"];
                selectedCollectionName = collection["collectionName"];
                selectedCollectionDescription = collection["description"]
                editCollection();
            });
            deleteButton.addEventListener('click', (event) => {
                event.stopPropagation();
                selectedCollection = collection["id"];
                selectedCollectionName = collection["collectionName"];
                deleteCollection(selectedCollectionName);
            });
        });
        const collectionContainer = document.getElementById('collection-container');
        collectionContainer.insertAdjacentHTML("beforeend",
            ' <input id="create-button" type="submit" onclick="createCollection()" value=" + Create">');
        collectionContainer.appendChild(ul);
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

    document.getElementById("collection-form").addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        fetch('/api/1.0/collections/create?userId=1', {
            method: 'POST',
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

    document.getElementById("collection-form").addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        fetch('/api/1.0/collections/update?collectionId=' + selectedCollection, {
            method: 'POST',
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
    fetch('/api/1.0/collections/delete?userId=1&collectionName=' + selectedCollectionName, {
        method: 'DELETE'
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
