$(document).on("pagecreate", "#search-page", function() {
    $("#bg-search-list").on("filterablebeforefilter", function (e, data) {
        var $ul = $(this),
            $input = $(data.input),
            value = $input.val(),
            html = "";
        $ul.html("");

        if (value && value.length > 0) {
            $ul.html("<li><div class='ui-loader'><span class='ui-icon ui-icon-loading'>Loading...</span></div></li>");
            $ul.listview("refresh");

			setTimeout(function() {
				if ($input.val() !== value) return;
				
	            $.ajax({
	                url: "api/v1/search?query=" + $input.val(),
	                dataType: "json"
	            })
	            .then(function (response) {
	                $.each(response.content, function(i, val) {
	                	html += 
							$("<li/>").append($("<a/>").attr("href", val.itemUrl)
								.append($("<h1/>").html(val.itemName))
								.append($("<h2/>").html(val.priceString + " <small>from " + val.storeName +"</small>"))
							)
							.prop('outerHTML');
					});
	                $ul.html(html);
	                $ul.listview("refresh");
	                $ul.trigger("updatelayout");
	            });
			}, 500);
			
        }
    });
});