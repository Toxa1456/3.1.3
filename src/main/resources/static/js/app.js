$(async function () {
    await getTableWithUsers();
    getDefaultModal();
    addNewUser();
    getNavBar();
})


const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    // bodyAdd : async function(user) {return {'method': 'POST', 'headers': this.head, 'body': user}},
    findAllUsers: async () => await fetch('admin/table'),
    findAuthUser: async () => await fetch('admin/user'),
    findOneUser: async (id) => await fetch(`admin/users/${id}`),
    addNewUser: async (user, role) => await fetch(`admin/users/${role}`, {method: 'POST', headers: userFetchService.head, body: JSON.stringify(user)}),
    updateUser: async (user, id, role) => await fetch(`admin/users/${id}/${role}`, {method: 'PUT', headers: userFetchService.head, body: JSON.stringify(user)}),
    deleteUser: async (id) => await fetch(`admin/users/${id}`, {method: 'DELETE', headers: userFetchService.head})
}

async function getTableWithUsers() {
    let table = $('#UserTable tbody');
    table.empty();

    await userFetchService.findAllUsers()
        .then(res => res.json())
        .then(users => {
            users.forEach(user => {
                let tableFilling = `$(
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>     
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td>
                                <button type="button" data-userid="${user.id}" data-action="edit" class="btn btn-info" 
                                data-toggle="modal" data-target="#someDefaultModal">Edit</button>
                            </td>
                            <td>
                                <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-danger" 
                                data-toggle="modal" data-target="#someDefaultModal">Delete</button>
                            </td>
                        </tr>
                )`;
                table.append(tableFilling);
            })
        })

    // обрабатываем нажатие на любую из кнопок edit или delete
    // достаем из нее данные и отдаем модалке, которую к тому же открываем
    $("#UserTable").find('button').on('click', (event) => {
        let defaultModal = $('#ModalWindow');

        let targetButton = $(event.target);
        let buttonUserId = targetButton.attr('data-userid');
        let buttonAction = targetButton.attr('data-action');

        defaultModal.attr('data-userid', buttonUserId);
        defaultModal.attr('data-action', buttonAction);
        defaultModal.modal('show');
    })
}

async function getNavBar() {
    let navBar = $('#navBar nav');
    navBar.empty();

    await userFetchService.findAuthUser()
        .then(res => res.json())
        .then(user => {
            let navBarUser = `
                <span style="color: white; font-weight: bold" >${user.email}</span>
                <span style="margin-right: 5px; margin-left: 5px; color: white">with roles:</span>
                <div style="color: white; margin-right: 5px" >${user.role}</div>
                <a class="text-end" style="color: gray; margin-left: auto" href="/logout">Logout</a>
            `;
            navBar.append(navBarUser);
        })
}



// что то деалем при открытии модалки и при закрытии
// основываясь на ее дата атрибутах
async function getDefaultModal() {
    $('#ModalWindow').modal({
        keyboard: true,
        backdrop: "static",
        show: false
    }).on("show.bs.modal", (event) => {
        let thisModal = $(event.target);
        let userid = thisModal.attr('data-userid');
        let action = thisModal.attr('data-action');
        switch (action) {
            case 'edit':
                editUser(thisModal, userid);
                break;
            case 'delete':
                deleteUser(thisModal, userid);
                break;
        }
    }).on("hidden.bs.modal", (e) => {
        let thisModal = $(e.target);
        thisModal.find('.modal-title').html('');
        thisModal.find('.modal-body').html('');
        thisModal.find('.modal-footer').html('');
    })
}


// редактируем юзера из модалки редактирования, забираем данные, отправляем
async function editUser(modal, id) {
    let preuser = await userFetchService.findOneUser(id);
    let user = preuser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button class="btn btn-primary" id="editButton">Edit</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let bodyForm = `
            <form class="form-group" id="editUser">
                <label for="id" class="col-form-label">ID</label><br>
                <input type="text" class="form-control" id="id" name="id" value="${user.id}" disabled><br>
                
                <label for="name">First Name</label>
                <input class="form-control" required type="text" id="name" value="${user.name}"><br>
                
                <label for="lastName">Last Name</label>
                <input class="form-control" required type="text" id="lastName" value="${user.lastName}"><br>
                
                <label for="age" class="col-form-label">Age</label>
                <input class="form-control" id="age" type="number" value="${user.age}"><br>
                
                <label for="email" class="col-form-label">Email</label>
                <input class="form-control" required type="email" id="email" value="${user.email}"><br>
                
                <label for="password" class="col-form-label">Password</label>
                <input class="form-control" type="password" id="password"><br>
                
                <label for="role" class="col-form-label">Role</label>
                <select id="role" size="2" required class="form-control" multiple>
                    <option value="1">User</option>
                    <option value="2">Admin</option>
                </select>
            </form>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#editButton").on('click', async () => {
        let id = modal.find("#id").val().trim();
        let name = modal.find("#name").val().trim();
        let lastName = modal.find("#lastName").val().trim();
        let age = modal.find("#age").val().trim();
        let email = modal.find("#email").val().trim()
        let password = modal.find("#password").val().trim();
        let role = modal.find("#role").val();
        let data = {
            id: id,
            name: name,
            lastName: lastName,
            age: age,
            email: email,
            password: password
        }
        const response = await userFetchService.updateUser(data, id, role);

        if (response.ok) {
            getTableWithUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}


// удаляем юзера из модалки удаления
async function deleteUser(modal, id) {
    let preuser = await userFetchService.findOneUser(id);
    let user = preuser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button class="btn btn-danger" id="deleteButton">Delete</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let bodyForm = `
            <form class="form-group" id="editUser">
                <label for="id" class="col-form-label">ID</label><br>
                <input type="text" class="form-control" id="id" name="id" value="${user.id}" disabled><br>
                
                <label for="name">First Name</label>
                <input class="form-control" required type="text" disabled id="name" value="${user.name}"><br>
                
                <label for="lastName">Last Name</label>
                <input class="form-control" required type="text" disabled id="lastName" value="${user.lastName}"><br>
                
                <label for="age" class="col-form-label">Age</label>
                <input class="form-control" id="age" type="number" disabled value="${user.age}"><br>
                
                <label for="email" class="col-form-label">Email</label>
                <input class="form-control" required type="email" disabled id="email" value="${user.email}"><br>
                
                <label for="role" class="col-form-label">Role</label>
                <select id="role" size="2" required class="form-control" disabled multiple>
                    <option value="1">User</option>
                    <option value="2">Admin</option>
                </select>
            </form>
        `;
        modal.find('.modal-body').append(bodyForm);
    })
    $("#deleteButton").on('click', async () => {
        await userFetchService.deleteUser(id);
        getTableWithUsers();
        modal.modal('hide');
    })
}






async function addNewUser() {
    $('#SubmitAddUser').click(async () =>  {
        let addUserForm = $('#AddUserForm')
        let name = addUserForm.find("#addName").val().trim();
        let lastName = addUserForm.find("#addLastName").val().trim();
        let age = addUserForm.find("#addAge").val().trim();
        let email = addUserForm.find("#addEmail").val().trim()
        let password = addUserForm.find("#addPassword").val().trim();
        let role = addUserForm.find("#addRole").val();
        let data = {
            name: name,
            lastName: lastName,
            age: age,
            email: email,
            password: password
        }
        const response = await userFetchService.addNewUser(data, role);
        if (response.ok) {
            getTableWithUsers();
            addUserForm.find('#addName').val('');
            addUserForm.find('#addLastName').val('');
            addUserForm.find('#addAge').val('');
            addUserForm.find('#addEmail').val('');
            addUserForm.find('#addPassword').val('')
            addUserForm.find('#addRole').val('');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert)
        }
    })
}