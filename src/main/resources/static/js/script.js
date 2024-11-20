console.log("script loaded");

let currTheme=getTheme();
changeTheme();

function changeTheme(){
	//set to web page
	document.querySelector('html').classList.add(currTheme);
	
	//set the listiner to change theme button
	const changeThemeButton=document.querySelector("#theme_change_button");
	changeThemeButton.addEventListener("click",(event)=>{
		const oldTheme=currTheme;
		console.log("Theme Changed clicked");
		
		
		if(currTheme=="dark"){
			currTheme="light";
		}else{
			currTheme="dark";
		}
		
		//change to local storage
		
		setTheme(currTheme);
		
		//remove the current theme
		document.querySelector("html").classList.remove(oldTheme);
		
		//set the current theme
		document.querySelector("html").classList.add(currTheme);
	})
	
}

//set theme to local storage
function setTheme(theme){
	localStorage.setItem("theme",theme);
}

//get theme from local storage
function getTheme(){
	let theme=localStorage.getItem("theme");
	if(theme) return theme;
	else return "light";
}