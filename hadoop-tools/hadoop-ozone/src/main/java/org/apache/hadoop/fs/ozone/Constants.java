begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
package|;
end_package

begin_comment
comment|/**  * Constants for Ozone FileSystem implementation.  */
end_comment

begin_class
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
DECL|field|OZONE_URI_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_SCHEME
init|=
literal|"o3"
decl_stmt|;
DECL|field|OZONE_DEFAULT_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_DEFAULT_USER
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|OZONE_HTTP_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HTTP_SCHEME
init|=
literal|"http://"
decl_stmt|;
DECL|field|OZONE_USER_DIR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_USER_DIR
init|=
literal|"/user"
decl_stmt|;
comment|/** Local buffer directory. */
DECL|field|BUFFER_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BUFFER_DIR_KEY
init|=
literal|"fs.ozone.buffer.dir"
decl_stmt|;
comment|/** Temporary directory. */
DECL|field|BUFFER_TMP_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BUFFER_TMP_KEY
init|=
literal|"hadoop.tmp.dir"
decl_stmt|;
DECL|field|OZONE_URI_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_DELIMITER
init|=
literal|"/"
decl_stmt|;
comment|/** Page size for Ozone listing operation. */
DECL|field|LISTING_PAGE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|LISTING_PAGE_SIZE
init|=
literal|1024
decl_stmt|;
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{    }
block|}
end_class

end_unit

