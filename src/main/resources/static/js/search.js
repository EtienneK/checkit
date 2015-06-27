$(function() {
	var viewModel = {};

	viewModel.results = ko.observableArray([]);
	viewModel.totalPages = ko.observable(0);
	viewModel.currentPage = ko.observable(-1);
	viewModel.nextPage = ko.observable(0);

	viewModel.isTypingInQueryInput = ko.observable(false);

	viewModel.query = ko.observable();

	viewModel.query.subscribe(function(value) {
		viewModel.updateData();
		viewModel.isTypingInQueryInput(true);
	}, viewModel);

	viewModel.delayedQuery = ko.computed(viewModel.query).extend({
		rateLimit : {
			method : "notifyWhenChangesStop",
			timeout : 550
		}
	});

	viewModel.delayedQuery.subscribe(function(value) {
		var query = value.toLowerCase().trim();

		if (query === '') {
			viewModel.updateData();
		} else {
			viewModel.fetchNextPage(query);
		}

	}, viewModel);

	viewModel.fetchNextPage = function(query) {
		query = query || viewModel.query();
		if (typeof query === 'object')
			query = viewModel.query();
		query = query.toLowerCase().trim();

		$.get('http://localhost:8080/api/v1/search', {
			query : query,
			page : viewModel.nextPage()
		}).done(function(data) {
			viewModel.updateData(data);
		});
	}

	viewModel.updateData = function(data) {
		data = data || {};
		if (data.content) {
			ko.utils.arrayPushAll(viewModel.results, data.content);
		} else {
			viewModel.results([]);
		}
		viewModel.totalPages(data.totalPages || 0);
		viewModel.currentPage(data.number === undefined ? -1 : data.number);
		viewModel.nextPage(viewModel.currentPage() + 1);
		viewModel.isTypingInQueryInput(false);
	};

	ko.applyBindings(viewModel);

});