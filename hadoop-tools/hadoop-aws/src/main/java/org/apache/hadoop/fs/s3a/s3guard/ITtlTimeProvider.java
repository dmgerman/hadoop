begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

begin_comment
comment|/**  * This interface is defined for handling TTL expiry of metadata in S3Guard.  *  * TTL can be tested by implementing this interface and setting is as  * {@code S3Guard.ttlTimeProvider}. By doing this, getNow() can return any  * value preferred and flaky tests could be avoided. By default getNow()  * returns the EPOCH in runtime.  *  * Time is measured in milliseconds,  */
end_comment

begin_interface
DECL|interface|ITtlTimeProvider
specifier|public
interface|interface
name|ITtlTimeProvider
block|{
DECL|method|getNow ()
name|long
name|getNow
parameter_list|()
function_decl|;
DECL|method|getMetadataTtl ()
name|long
name|getMetadataTtl
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

