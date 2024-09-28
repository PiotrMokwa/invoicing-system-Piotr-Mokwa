CREATE TABLE public.vat
(
 id bigserial NOT NULL,
 name character varying(10) NOT NULL,
 vat_value numeric(2) NOT NULL,
 PRIMARY KEY (id)
);

INSERT INTO public.vat (name,vat_value)
    values  ('vat_23',23),
            ('vat_8',8),
            ('vat_5',5),
            ('vat_0',0);


