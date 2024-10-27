const loadCompanies = async ()=>{

       const response = await fetch('http://localhost:5555/companies');
       const companies = await response.json();
       const companiesTable = document.getElementById("companiesTable")

         for(const company of companies){
             const row = companiesTable.insertRow(-1);

             const id = row.insertCell(0)
             id.innerText = company.id

             const nameCell = row.insertCell(1)
             nameCell.innerText = company.name

             const taxIdCell = row.insertCell(2)
             taxIdCell.innerText = company.taxIdentification

             const addressCELL = row.insertCell(3)
             addressCELL.innerText = company.address


             const pensionInsuranceCell = row.insertCell(4)
             pensionInsuranceCell.innerText = company.pensionInsurance

             const healthInsuranceBaseValue = row.insertCell(5)
             healthInsuranceBaseValue.innerText = company.healthInsuranceBaseValue
             console.log(company);

         };

       }

    const serializeFormToJson = form => JSON.stringify(
        Array.from(new FormData(form).entries())
            .reduce((m, [key, value]) =>
                Object.assign(m, {[key]: value}), {})
    );

      function handleAddCompanyFormSubmit() {
          const form = $("#addCompanyForm");
          form.on('submit', function (e) {
              e.preventDefault();

              $.ajax({
                  url: 'companies',
                  type: 'post',
                  contentType: 'application/json',
                  data: serializeFormToJson(this),
                  success: function (data) {
                      $("#companiesTable").find("tr:gt(0)").remove();
                      loadCompanies()
                  },
                  error: function (jqXhr, textStatus, errorThrown) {
                      alert(errorThrown)
                  }
              });
          });
      }

       window.onload = function() {
         loadCompanies();
         handleAddCompanyFormSubmit()
       }