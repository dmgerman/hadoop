begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
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
name|security
operator|.
name|NoSuchAlgorithmException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
import|;
end_import

begin_comment
comment|/** Thrown for checksum errors. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|OzoneChecksumException
specifier|public
class|class
name|OzoneChecksumException
extends|extends
name|IOException
block|{
comment|/**    * OzoneChecksumException to throw when checksum verfication fails.    * @param index checksum list index at which checksum match failed    */
DECL|method|OzoneChecksumException (int index)
specifier|public
name|OzoneChecksumException
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Checksum mismatch at index %d"
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * OzoneChecksumException to throw when unrecognized checksumType is given.    * @param unrecognizedChecksumType    */
DECL|method|OzoneChecksumException ( ContainerProtos.ChecksumType unrecognizedChecksumType)
specifier|public
name|OzoneChecksumException
parameter_list|(
name|ContainerProtos
operator|.
name|ChecksumType
name|unrecognizedChecksumType
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unrecognized ChecksumType: %s"
argument_list|,
name|unrecognizedChecksumType
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * OzoneChecksumException to wrap around NoSuchAlgorithmException.    * @param algorithm name of algorithm    * @param ex original exception thrown    */
DECL|method|OzoneChecksumException ( String algorithm, NoSuchAlgorithmException ex)
specifier|public
name|OzoneChecksumException
parameter_list|(
name|String
name|algorithm
parameter_list|,
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"NoSuchAlgorithmException thrown while computing "
operator|+
literal|"SHA-256 checksum using algorithm %s"
argument_list|,
name|algorithm
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|/**    * OzoneChecksumException to throw with custom message.    */
DECL|method|OzoneChecksumException (String message)
specifier|public
name|OzoneChecksumException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

