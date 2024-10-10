ALTER TABLE public.invoice_entry
ADD COLUMN vat_rate_temp character varying (10);

UPDATE public.invoice_entry
set vat_rate_temp = concat('VAT_',(select name from vat where invoice_entry.vat_rate = id ));

ALTER TABLE public.invoice_entry
DROP vat_rate;

DROP TABLE public.vat;

ALTER TABLE public.invoice_entry
ADD COLUMN vat_rate character varying(10);

UPDATE public.invoice_entry
SET vat_rate = vat_rate_temp;

ALTER TABLE public.invoice_entry
DROP vat_rate_temp;

ALTER TABLE public.invoice_entry
ALTER COLUMN vat_rate SET NOT NULL;


