begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.appcatalog.controller
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|controller
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|POST
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PathParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|QueryParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|application
operator|.
name|AppCatalogSolrClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|AppStoreEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|appcatalog
operator|.
name|model
operator|.
name|Application
import|;
end_import

begin_comment
comment|/**  * Application catalog REST API for searching and recommending  * applications.  *  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/app_store"
argument_list|)
DECL|class|AppStoreController
specifier|public
class|class
name|AppStoreController
block|{
DECL|method|AppStoreController ()
specifier|public
name|AppStoreController
parameter_list|()
block|{   }
comment|/**    * Display the most frequently used applications on YARN AppCatalog home page.    *    * @apiGroup AppStoreController    * @apiName get    * @api {get} /app_store/recommended  Display recommended applications.    * @apiSuccess {Object} AppEntry Application configuration.    * @apiSuccessExample {json} Success-Response:    *     HTTP/1.1 200 OK    *     [    *        {    *           "id":"96b7833a-e3",    *           "org":"Hortonworks",    *           "name":"LAMP",    *           "desc":"Linux Apache MySQL PHP web application",    *           "icon":"/css/img/feather.png",    *           "like":0,    *           "download":0,    *           "app":null    *        },    *        {    *        ...    *        }    *     ]    * @return - List of YARN applications    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"recommended"
argument_list|)
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
DECL|method|get ()
specifier|public
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|get
parameter_list|()
block|{
name|AppCatalogSolrClient
name|sc
init|=
operator|new
name|AppCatalogSolrClient
argument_list|()
decl_stmt|;
return|return
name|sc
operator|.
name|getRecommendedApps
argument_list|()
return|;
block|}
comment|/**    * Search for yarn applications from solr.    *    * @apiGroup AppStoreController    * @apiName search    * @api {get} /app_store/search  Find application from appstore.    * @apiParam {String} q Keyword to search.    * @apiSuccess {Object} AppStoreEntry List of matched applications.    * @apiSuccessExample {json} Success-Response:    *     HTTP/1.1 200 OK    *     [    *        {    *           "id":"96b7833a-e3",    *           "org":"Hortonworks",    *           "name":"LAMP",    *           "desc":"Linux Apache MySQL PHP web application",    *           "icon":"/css/img/feather.png",    *           "like":0,    *           "download":0,    *           "app":null    *        },    *        {    *        ...    *        }    *     ]    * @param keyword - search for keyword    * @return - List of YARN applications matching keyword search.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"search"
argument_list|)
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
DECL|method|search (@ueryParamR) String keyword)
specifier|public
name|List
argument_list|<
name|AppStoreEntry
argument_list|>
name|search
parameter_list|(
annotation|@
name|QueryParam
argument_list|(
literal|"q"
argument_list|)
name|String
name|keyword
parameter_list|)
block|{
name|AppCatalogSolrClient
name|sc
init|=
operator|new
name|AppCatalogSolrClient
argument_list|()
decl_stmt|;
return|return
name|sc
operator|.
name|search
argument_list|(
name|keyword
argument_list|)
return|;
block|}
comment|/**    * Find yarn application from solr.    *    * @param id Application ID    * @return AppEntry    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"get/{id}"
argument_list|)
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
DECL|method|get (@athParamR) String id)
specifier|public
name|AppStoreEntry
name|get
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"id"
argument_list|)
name|String
name|id
parameter_list|)
block|{
name|AppCatalogSolrClient
name|sc
init|=
operator|new
name|AppCatalogSolrClient
argument_list|()
decl_stmt|;
return|return
name|sc
operator|.
name|findAppStoreEntry
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Register an application.    *    * @apiGroup AppStoreController    * @apiName register    * @api {post} /app_store/register  Register an application in appstore.    * @apiParam {Object} app Application definition.    * @apiParamExample {json} Request-Example:    *     {    *       "name": "Jenkins",    *       "organization": "Jenkins-ci.org",    *       "description": "The leading open source automation server",    *       "icon": "/css/img/jenkins.png",    *       "lifetime": "3600",    *       "components": [    *         {    *           "name": "jenkins",    *           "number_of_containers": 1,    *           "artifact": {    *             "id": "eyang-1.openstacklocal:5000/jenkins:latest",    *             "type": "DOCKER"    *           },    *           "launch_command": "",    *           "resource": {    *             "cpus": 1,    *             "memory": "2048"    *           },    *           "configuration": {    *             "env": {    *             },    *             "files": [    *             ]    *           }    *         }    *       ],    *       "quicklinks": {    *         "Jenkins UI": "http://jenkins.${SERVICE_NAME}.${USER}.${DOMAIN}:8080/"    *       }    *     }    * @apiSuccess {String} Response Application register result.    * @apiError BadRequest Error in process application registration.    * @param app - Yarnfile in JSON form    * @return Web response    */
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
literal|"register"
argument_list|)
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
DECL|method|register (Application app)
specifier|public
name|Response
name|register
parameter_list|(
name|Application
name|app
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|app
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Application name can not be empty."
argument_list|)
throw|;
block|}
if|if
condition|(
name|app
operator|.
name|getOrganization
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Application organization can not be empty."
argument_list|)
throw|;
block|}
if|if
condition|(
name|app
operator|.
name|getDescription
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Application description can not be empty."
argument_list|)
throw|;
block|}
name|AppCatalogSolrClient
name|sc
init|=
operator|new
name|AppCatalogSolrClient
argument_list|()
decl_stmt|;
name|sc
operator|.
name|register
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|BAD_REQUEST
argument_list|)
operator|.
name|entity
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|ACCEPTED
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

