begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|interface|MimeType
specifier|public
interface|interface
name|MimeType
block|{
DECL|field|TEXT
specifier|public
specifier|static
specifier|final
name|String
name|TEXT
init|=
literal|"text/plain; charset=UTF-8"
decl_stmt|;
DECL|field|HTML
specifier|public
specifier|static
specifier|final
name|String
name|HTML
init|=
literal|"text/html; charset=UTF-8"
decl_stmt|;
DECL|field|XML
specifier|public
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"text/xml; charset=UTF-8"
decl_stmt|;
DECL|field|HTTP
specifier|public
specifier|static
specifier|final
name|String
name|HTTP
init|=
literal|"message/http; charset=UTF-8"
decl_stmt|;
DECL|field|JSON
specifier|public
specifier|static
specifier|final
name|String
name|JSON
init|=
literal|"application/json; charset=UTF-8"
decl_stmt|;
block|}
end_interface

end_unit

