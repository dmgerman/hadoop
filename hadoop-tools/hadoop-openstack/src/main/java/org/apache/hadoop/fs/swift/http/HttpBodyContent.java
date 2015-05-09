begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|http
package|;
end_package

begin_comment
comment|/**  * Response tuple from GET operations; combines the input stream with the content length  */
end_comment

begin_class
DECL|class|HttpBodyContent
specifier|public
class|class
name|HttpBodyContent
block|{
DECL|field|contentLength
specifier|private
specifier|final
name|long
name|contentLength
decl_stmt|;
DECL|field|inputStream
specifier|private
specifier|final
name|HttpInputStreamWithRelease
name|inputStream
decl_stmt|;
comment|/**    * build a body response    * @param inputStream input stream from the operation    * @param contentLength length of content; may be -1 for "don't know"    */
DECL|method|HttpBodyContent (HttpInputStreamWithRelease inputStream, long contentLength)
specifier|public
name|HttpBodyContent
parameter_list|(
name|HttpInputStreamWithRelease
name|inputStream
parameter_list|,
name|long
name|contentLength
parameter_list|)
block|{
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|this
operator|.
name|inputStream
operator|=
name|inputStream
expr_stmt|;
block|}
DECL|method|getContentLength ()
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
return|return
name|contentLength
return|;
block|}
DECL|method|getInputStream ()
specifier|public
name|HttpInputStreamWithRelease
name|getInputStream
parameter_list|()
block|{
return|return
name|inputStream
return|;
block|}
block|}
end_class

end_unit

