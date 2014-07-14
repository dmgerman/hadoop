begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Defines the types of supported storage media. The default storage  * medium is assumed to be DISK.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|StorageType
specifier|public
enum|enum
name|StorageType
block|{
DECL|enumConstant|DISK
name|DISK
block|,
DECL|enumConstant|SSD
name|SSD
block|,
DECL|enumConstant|ARCHIVE
name|ARCHIVE
block|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|StorageType
name|DEFAULT
init|=
name|DISK
decl_stmt|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|StorageType
index|[]
name|EMPTY_ARRAY
init|=
block|{}
decl_stmt|;
block|}
end_enum

end_unit

