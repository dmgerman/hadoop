begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web
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
operator|.
name|web
package|;
end_package

begin_comment
comment|/*     ,  );   long now = System.currentTimeMillis();   httpRes.addDateHeader ( "Expires", now );   httpRes.addDateHeader ( "Date", now );   httpRes.addHeader ( "Pragma", "no-cache" );  */
end_comment

begin_interface
DECL|interface|HttpCacheHeaders
specifier|public
interface|interface
name|HttpCacheHeaders
block|{
DECL|field|HTTP_HEADER_CACHE_CONTROL
name|String
name|HTTP_HEADER_CACHE_CONTROL
init|=
literal|"Cache-Control"
decl_stmt|;
DECL|field|HTTP_HEADER_CACHE_CONTROL_NONE
name|String
name|HTTP_HEADER_CACHE_CONTROL_NONE
init|=
literal|"no-cache"
decl_stmt|;
DECL|field|HTTP_HEADER_PRAGMA
name|String
name|HTTP_HEADER_PRAGMA
init|=
literal|"Pragma"
decl_stmt|;
block|}
end_interface

end_unit

