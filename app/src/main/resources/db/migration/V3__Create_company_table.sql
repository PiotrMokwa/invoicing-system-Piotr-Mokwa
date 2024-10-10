CREATE TABLE public.company
(
        id bigserial NOT NULL,
        tax_identification character varying(100) NOT NULL,
        name character varying(200) NOT NULL,
        address character varying(250) NOT NULL,
        health_insurance_base_value numeric(10,2) NOT NULL,
        pension_insurance numeric(10,2) NOT NULL,
        amount_of_health_insurance numeric(10,2) NOT NULL,
        amount_of_health_insurance_to_reduce_tax numeric(10,2) NOT NULL,

        PRIMARY KEY (id)
);

