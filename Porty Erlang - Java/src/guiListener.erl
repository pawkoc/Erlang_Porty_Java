-module(guiListener).
-export([start/0, init/0, loop/1]).

-include("../deps/piqirun.hrl").
-include("../deps/addressbook_piqi.hrl").

start() ->
	init().

init() ->
	Port = open_port({spawn,
		"java -cp .:../deps ServerGUI"}, [{packet, 4}, binary]),
	link(Port),
	loop(Port).

loop(Port) ->
	receive
		{Port, {data, <<"add">>}} ->
			receive
				{Port, {data, Bytes}} ->
					bookServ:addPerson(addressbook_piqi:parse_person(Bytes)),
					loop(Port)
			end;
		{Port, {data, <<"show">>}} ->
			Bytes = addressbook_piqi:gen_address_book(bookServ:getBook()),
			port_command(Port, Bytes),
			loop(Port);
		{Port, {data, <<"close">>}} ->
			exit(whereis(bookSup), shutdown),
			exit(whereis(bookServ), shutdown),
			exit(shutdown)
	end.