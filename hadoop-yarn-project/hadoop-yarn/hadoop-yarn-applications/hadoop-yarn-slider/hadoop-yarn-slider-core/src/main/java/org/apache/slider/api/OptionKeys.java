begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  *  Keys for entries in the<code>options</code> section  *  of a cluster description.  */
end_comment

begin_interface
DECL|interface|OptionKeys
specifier|public
interface|interface
name|OptionKeys
extends|extends
name|InternalKeys
block|{
comment|/**    * Time in milliseconds to wait after forking any in-AM     * process before attempting to start up the containers: {@value}    *     * A shorter value brings the cluster up faster, but means that if the    * in AM process fails (due to a bad configuration), then time    * is wasted starting containers on a cluster that isn't going to come    * up    */
DECL|field|APPLICATION_TYPE
name|String
name|APPLICATION_TYPE
init|=
literal|"application.type"
decl_stmt|;
DECL|field|APPLICATION_NAME
name|String
name|APPLICATION_NAME
init|=
literal|"application.name"
decl_stmt|;
comment|/**    * Prefix for site.xml options: {@value}    */
DECL|field|SITE_XML_PREFIX
name|String
name|SITE_XML_PREFIX
init|=
literal|"site."
decl_stmt|;
comment|/**    * Prefix for config file options: {@value}    */
DECL|field|CONF_FILE_PREFIX
name|String
name|CONF_FILE_PREFIX
init|=
literal|"conf."
decl_stmt|;
comment|/**    * Prefix for package options: {@value}    */
DECL|field|PKG_FILE_PREFIX
name|String
name|PKG_FILE_PREFIX
init|=
literal|"pkg."
decl_stmt|;
comment|/**    * Prefix for export options: {@value}    */
DECL|field|EXPORT_PREFIX
name|String
name|EXPORT_PREFIX
init|=
literal|"export."
decl_stmt|;
comment|/**    * Type suffix for config file and package options: {@value}    */
DECL|field|TYPE_SUFFIX
name|String
name|TYPE_SUFFIX
init|=
literal|".type"
decl_stmt|;
comment|/**    * Name suffix for config file and package options: {@value}    */
DECL|field|NAME_SUFFIX
name|String
name|NAME_SUFFIX
init|=
literal|".name"
decl_stmt|;
comment|/**    * Per component suffix for config file options: {@value}    */
DECL|field|PER_COMPONENT
name|String
name|PER_COMPONENT
init|=
literal|".per.component"
decl_stmt|;
comment|/**    * Per group suffix for config file options: {@value}    */
DECL|field|PER_GROUP
name|String
name|PER_GROUP
init|=
literal|".per.group"
decl_stmt|;
comment|/**    * Zookeeper quorum host list: {@value}    */
DECL|field|ZOOKEEPER_QUORUM
name|String
name|ZOOKEEPER_QUORUM
init|=
literal|"zookeeper.quorum"
decl_stmt|;
DECL|field|ZOOKEEPER_HOSTS
name|String
name|ZOOKEEPER_HOSTS
init|=
literal|"zookeeper.hosts"
decl_stmt|;
DECL|field|ZOOKEEPER_PORT
name|String
name|ZOOKEEPER_PORT
init|=
literal|"zookeeper.port"
decl_stmt|;
comment|/**    * Zookeeper path value (string): {@value}    */
DECL|field|ZOOKEEPER_PATH
name|String
name|ZOOKEEPER_PATH
init|=
literal|"zookeeper.path"
decl_stmt|;
block|}
end_interface

end_unit

