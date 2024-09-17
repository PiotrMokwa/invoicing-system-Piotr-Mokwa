CREATE TABLE public.car
(
  id bigserial NOT NULL,
  is_private_Use boolean NOT NULL DEFAULT false,
  car_Registration_Number character varying(10) NOT NULL,
 PRIMARY KEY (id)
);

ALTER TABLE public.car
    OWNER to postgres