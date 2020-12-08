
 //Sidebar Code

const toggleSidebar = () =>{
    
    if($(".sidebar").is(":visible")){

    $(".sidebar").css("display","none");
    $(".content").css("margin-left","0%");
    
    }
    else{

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");


    }
};

// Search functionality code

const Search = () =>{
	
    let query=$("#search-input").val();

    if(query==""){
        $(".search-result").hide();
    }
    else{
     
    let url = `https://smartcontactmanager.herokuapp.com/search/${query}`;

        fetch(url)
        .then((response) => {
            return response.json();
        }).then((data) => {

        let text=`<div class='list-group'>`;

        data.forEach((contacts) => {
            text+=`<a href='/user/${contacts.cid}/contact_profile' class='list-group-item list-group-item-action'>${contacts.name}</a>`;

        });
        
        text+=`</div>`

        $(".search-result").html(text);
        $(".search-result").show();
    });

    
    }
};
