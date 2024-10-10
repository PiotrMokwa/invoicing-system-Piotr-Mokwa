CREATE TABLE public.vat
(
 id bigserial NOT NULL,
 name character varying(10) NOT NULL,
 vat_value numeric(2) NOT NULL,
 PRIMARY KEY (id)
);

INSERT INTO public.vat (name,vat_value)
    values  ('23',23),
            ('8',8),
            ('5',5),
            ('0',0);


