CREATE TABLE public.invoice_entry
(
    id              bigserial              NOT NULL,
    description     character varying(100) NOT NULL,
    quantity        numeric(5,2)           NOT NULL,
    price           numeric(10,2)          NOT NULL,
    vat_value       numeric(10,2)          NOT NULL,
    vat_rate        bigint                 NOT NULL,
    expanse_for_car bigint                 NOT NULL,
        PRIMARY KEY (id)
);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT expanse_for_car_fk FOREIGN KEY (expanse_for_car)
    REFERENCES public.car (id)
    ON DELETE CASCADE;




