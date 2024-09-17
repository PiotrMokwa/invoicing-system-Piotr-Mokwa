CREATE TABLE public.invoice_entry
(
    id              bigserial              NOT NULL,
    description     character varying(100) NOT NULL,
    quantity        numeric(5,0)           NOT NULL,
    price           numeric(10,2)          NOT NULL,
    vat_value       bigint                 NOT NULL,
    expanse_for_car bigint                 NOT NULL,
        PRIMARY KEY (id)
);

ALTER TABLE public.invoice_entry
    OWNER to postgres;

ALTER TABLE public.invoice_entry
   ADD CONSTRAINT vat_value_fk FOREIGN KEY (vat_value)
   REFERENCES public.vat (id);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT expanse_for_car_fk FOREIGN KEY (expanse_for_car)
    REFERENCES public.car (id);

ALTER TABLE public.invoice_entry
    OWNER to postgres;



