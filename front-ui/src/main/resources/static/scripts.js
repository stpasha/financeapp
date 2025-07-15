console.log("Скрипт загружен!");
document.addEventListener("DOMContentLoaded", function () {
    const toLoginSelect = document.querySelector('#to_login');
    const targetAccountSelect = document.querySelector('#targetTransferAccountId');

    toLoginSelect.addEventListener("change", function () {
        const selectedUserId = this.value;
        if (!selectedUserId) return;

        targetAccountSelect.innerHTML = '<option disabled selected>Загрузка...</option>';

        fetch(`/account/${selectedUserId}`)
            .then(response => {
                if (!response.ok) throw new Error("Ошибка при получении счетов");
                return response.json();
            })
            .then(accountList => {
                targetAccountSelect.innerHTML = "";
                if (accountList.length === 0) {
                    targetAccountSelect.innerHTML = '<option disabled>Нет доступных счетов</option>';
                    return;
                }

                accountList.forEach(account => {
                    const option = document.createElement("option");
                    option.value = account.id;
                    option.text = `${account.currencyCode} (${account.id})`;
                    targetAccountSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Ошибка:", error);
                targetAccountSelect.innerHTML = '<option disabled>Ошибка загрузки счетов</option>';
            });
    });
});

setInterval(() => {
    const td = document.getElementById('exchange_rates');
    fetch('http://finance.local/rates')
        .then(response => response.json())
        .then(json => {
            let table = `
                <div class="table-responsive">
                    <table class="table table-sm table-striped table-bordered align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th colspan="3" class="text-center">Курсы валют по отношению к рублю</th>
                            </tr>
                            <tr>
                                <th>Валюта</th>
                                <th>Обозначение</th>
                                <th>Курс</th>
                            </tr>
                        </thead>
                        <tbody>
            `;
            json.forEach(rate => {
                table += `
                    <tr>
                        <td>${rate.name}</td>
                        <td>${rate.code}</td>
                        <td>${rate.value}</td>
                    </tr>
                `;
            });
            table += `
                        </tbody>
                    </table>
                </div>
            `;
            td.innerHTML = table;
        })
        .catch(error => {
            console.error("Ошибка при получении курсов:", error);
            td.innerHTML = `<div class="alert alert-danger text-center" role="alert">Ошибка при получении курсов валют</div>`;
        });
}, 1000);
