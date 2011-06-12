begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.example
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|example
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|DocumentAndOp
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|DocumentID
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|ILocalAnalysis
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|OutputCollector
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
name|mapred
operator|.
name|Reporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  * Convert LineDocTextAndOp to DocumentAndOp as required by ILocalAnalysis.  */
end_comment

begin_class
DECL|class|LineDocLocalAnalysis
specifier|public
class|class
name|LineDocLocalAnalysis
implements|implements
name|ILocalAnalysis
argument_list|<
name|DocumentID
argument_list|,
name|LineDocTextAndOp
argument_list|>
block|{
DECL|field|docidFieldName
specifier|private
specifier|static
name|String
name|docidFieldName
init|=
literal|"id"
decl_stmt|;
DECL|field|contentFieldName
specifier|private
specifier|static
name|String
name|contentFieldName
init|=
literal|"content"
decl_stmt|;
comment|/* (non-Javadoc)    * @see org.apache.hadoop.mapred.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapred.OutputCollector, org.apache.hadoop.mapred.Reporter)    */
DECL|method|map (DocumentID key, LineDocTextAndOp value, OutputCollector<DocumentID, DocumentAndOp> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|DocumentID
name|key
parameter_list|,
name|LineDocTextAndOp
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|DocumentID
argument_list|,
name|DocumentAndOp
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentAndOp
operator|.
name|Op
name|op
init|=
name|value
operator|.
name|getOp
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|Term
name|term
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|op
operator|==
name|DocumentAndOp
operator|.
name|Op
operator|.
name|INSERT
operator|||
name|op
operator|==
name|DocumentAndOp
operator|.
name|Op
operator|.
name|UPDATE
condition|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|docidFieldName
argument_list|,
name|key
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|contentFieldName
argument_list|,
name|value
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|==
name|DocumentAndOp
operator|.
name|Op
operator|.
name|DELETE
operator|||
name|op
operator|==
name|DocumentAndOp
operator|.
name|Op
operator|.
name|UPDATE
condition|)
block|{
name|term
operator|=
operator|new
name|Term
argument_list|(
name|docidFieldName
argument_list|,
name|key
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|DocumentAndOp
argument_list|(
name|op
argument_list|,
name|doc
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.mapred.JobConfigurable#configure(org.apache.hadoop.mapred.JobConf)    */
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{   }
comment|/* (non-Javadoc)    * @see org.apache.hadoop.io.Closeable#close()    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class

end_unit

