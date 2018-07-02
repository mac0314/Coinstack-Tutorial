FlowRouter.route('/', {
    name: 'App.home',
    action: function(params, queryParams) {
        console.log("Hello world!");
    }
});