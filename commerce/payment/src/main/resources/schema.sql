create table if not exists payments
(
    payment_id     uuid default gen_random_uuid() primary key,
    order_id       uuid not null,
    products_total numeric(19,4),
    delivery_total numeric(19,4),
    total_payment  numeric(19,4),
    fee_total      numeric(19,4),
    status         varchar(15)
);