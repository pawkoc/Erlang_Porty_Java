-module(bookServ).
-behaviour(gen_server).
-export([init/1, terminate/2, handle_cast/2, handle_call/3]).
-export([start_link/1]).
-export([addPerson/1, getBook/0]).

-include("../deps/piqirun.hrl").
-include("../deps/addressbook_piqi.hrl").

start_link(Book) ->
	gen_server:start_link(
		{local, bookServ},
		?MODULE,
		Book, []).

terminate(Reason, Book) ->
	ok.

init(Book) ->
	{ok, Book}.

addPerson(Person) ->
	gen_server:cast(bookServ, {addPerson, Person}).

getBook() ->
	gen_server:call(bookServ, getBook).

handle_cast({addPerson, Person}, Book) ->
	{noreply, #address_book{
        person = Book#address_book.person ++ [Person]
    }}.

handle_call(getBook, _, Book) ->
	{reply, Book, Book}.

