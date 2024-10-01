CREATE TABLE public.companies
(
        id bigserial NOT NULL,
        tax_identification character varying(100) NOT NULL,
        name character varying(200) NOT NULL,
        address character varying(250) NOT NULL,
        healthInsuranceBaseValue numeric(10,2) NOT NULL,
        pensionInsurance numeric(10,2) NOT NULL,
        amountOfHealthInsurance numeric(10,2) NOT NULL,
        amountOfHealthInsuranceToReduceTax numeric(10,2) NOT NULL,

        PRIMARY KEY (id)
);

