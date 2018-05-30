/*
Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
For licensing, see license.txt or http://cksource.com/ckfinder/license
*/

CKFinder.customConfig = function( config )
{
	// Define changes to default configuration here.
	// For the list of available options, check:
	// http://docs.cksource.com/ckfinder_2.x_api/symbols/CKFinder.config.html

	// Sample configuration options:
	// config.uiColor = '#BDE31E';
	// config.language = 'fr';
	config.removePlugins = 'basket';//'help,basket,flashupload';
	config.skin = 'bootstrap';
	//config.extraPlugins = 'imageresize';
	//config.logStackTrace = true;
	config.disableHelpButton=true;
	//config.connectorInfo = 'scale=300x300&page=Animals';
	config.defaultSortBy = 'date';
	config.disableThumbnailSelection = false;
	config.defaultViewType_Files = 'list';
};
