CREATE TABLE public.companies
(
        id bigserial NOT NULL,
        tax_identyfication character varying(100) NOT NULL,
        name character varying(200) NOT NULL,
        address character varying(250) NOT NULL,
        PRIMARY KEY (id)
);

ALTER TABLE public.companies
OWNER to postgres;