-ifndef(__ADDRESSBOOK_PIQI_HRL__).
-define(__ADDRESSBOOK_PIQI_HRL__, 1).


-record(person, {
    name :: string() | binary(),
    id :: integer(),
    email :: string() | binary(),
    phone = [] :: [person_phone_number()]
}).

-record(person_phone_number, {
    number :: string() | binary(),
    type = home :: person_phone_type() | 'undefined'
}).

-type person_phone_type() ::
      mobile
    | home
    | work.

-record(address_book, {
    person = [] :: [person()]
}).

-type person() :: #person{}.

-type person_phone_number() :: #person_phone_number{}.

-type address_book() :: #address_book{}.


-endif.
