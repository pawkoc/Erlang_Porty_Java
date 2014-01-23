-module(bookSup).
-behaviour(supervisor).
-export([start/0, init/1]).

-include("../deps/piqirun.hrl").
-include("../deps/addressbook_piqi.hrl").

start() ->
	register(guiListener, spawn_link(guiListener, start, [])),
	supervisor:start_link({local, bookSup}, ?MODULE, [#address_book{}]).

init(Book) ->
	{ok, {
		{one_for_one, 10, 2000},
		[{bookServ, {bookServ, start_link, Book}, permanent, brutal_kill, worker, [bookServ]}]}
	}.


