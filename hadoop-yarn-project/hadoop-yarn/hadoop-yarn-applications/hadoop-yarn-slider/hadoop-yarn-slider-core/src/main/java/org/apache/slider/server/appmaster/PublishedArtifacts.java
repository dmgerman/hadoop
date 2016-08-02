begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
package|;
end_package

begin_comment
comment|/**  * This is the name of YARN artifacts that are published  */
end_comment

begin_interface
DECL|interface|PublishedArtifacts
specifier|public
interface|interface
name|PublishedArtifacts
block|{
DECL|field|COMPLETE_CONFIG
name|String
name|COMPLETE_CONFIG
init|=
literal|"complete-config"
decl_stmt|;
DECL|field|CORE_SITE_CONFIG
name|String
name|CORE_SITE_CONFIG
init|=
literal|"core-site"
decl_stmt|;
DECL|field|HDFS_SITE_CONFIG
name|String
name|HDFS_SITE_CONFIG
init|=
literal|"hdfs-site"
decl_stmt|;
DECL|field|YARN_SITE_CONFIG
name|String
name|YARN_SITE_CONFIG
init|=
literal|"yarn-site"
decl_stmt|;
DECL|field|LOG4J
name|String
name|LOG4J
init|=
literal|"log4j"
decl_stmt|;
block|}
end_interface

end_unit

