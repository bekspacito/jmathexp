package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

class LexicalAnalizer{

        private Scanner scanner;
        private int frontierCharIndex;
        private String exp;
        private List<String> functions;
        private Set<String> rsOperators;
        private Set<String> lsOperators;
        private Set<String> binaryOperators;

        private List<String> operators;
        private List<String> variables;
        private List<Token>  reserveSymbols;

        public LexicalAnalizer(String exp,
                               Set<String> variables,
                               Informator informator)
        {

            if(exp == null || exp.isEmpty())
                throw new IllegalArgumentException("math expression cannot be null nor empty...");

            Set<String> functions       = informator.lexemesOf(Token.Type.FUNCTION);
            Set<String> rsOperators     = informator.lexemesOf(Token.Type.RS_UNARY_OPERATOR);
            Set<String> lsOperators     = informator.lexemesOf(Token.Type.LS_UNARY_OPERATOR);
            Set<String> binaryOperators = informator.lexemesOf(Token.Type.BINARY_OPERATOR);

            this.exp = exp.replaceAll("\\s+","") + "]";
            scanner = new Scanner(this.exp);
            frontierCharIndex = 0;

            this.binaryOperators = binaryOperators;
            this.rsOperators = rsOperators;
            this.lsOperators = lsOperators;

            //we sort operators and functions by length to apply LONGEST MATCHING RULE
            this.functions = functions.stream()
                                        .sorted(comparingInt(String::length).reversed())
                                        .collect(toList());

            operators = Stream.of(rsOperators,lsOperators,binaryOperators)
                                .flatMap(Set::stream)
                                .distinct()
                                .sorted(comparingInt(String::length).reversed())
                                .collect(toList());

            if(variables != null)
            this.variables = variables.stream()
                                        .sorted(comparingInt(String::length).reversed())
                                        .collect(toList());

            reserveSymbols = asList(new Token(Token.Type.OPEN_PARENTHESES,"("),
                                    new Token(Token.Type.CLOSE_PARENTHESES,")"),
                                    new Token(Token.Type.FUNCTION_ARG_SEPARATOR,","),
                                    new Token(Token.Type.END,"]"));

        }

        public boolean hasNext(){ return frontierCharIndex < exp.length(); }

        public List<Token> next(){

            if(frontierCharIndex >= exp.length())
                throw new NoSuchElementException("no tokens left....");

            List<Token> result = new ArrayList<>();
            String nextTokenStr = null;

            //is operand
            if(Character.isDigit(exp.charAt(frontierCharIndex))) {
                nextTokenStr = scanner.findInLine("([0-9]+(\\.[0-9]+)?|\\.[0-9]+)");
                frontierCharIndex += nextTokenStr.length();
                result.add(new Token(Token.Type.OPERAND,nextTokenStr));
                return result;
            }

            if(Character.isLetter(exp.charAt(frontierCharIndex))) {
                Optional<String> nextToken = findFunction();
                if(nextToken.isPresent()) {
                    frontierCharIndex += nextToken.get().length();
                    result.add(new Token(Token.Type.FUNCTION, nextToken.get()));
                    return result;
                }

                nextToken = findVariable();
                if(nextToken.isPresent()){
                    frontierCharIndex += nextToken.get().length();
                    result.add(new Token(Token.Type.VARIABLE, nextToken.get()));
                    return result;
                }
            }

            //is operator
            Optional<String> res = findOperator();
            if(res.isPresent()) {
                frontierCharIndex += res.get().length();
                return getMatchedTokens(res.get());
            }


            //is reversed symbols
            Optional<Token> resToken = reserveSymbols.stream()
                    .filter(t -> scanner.findWithinHorizon("\\Q" + t.lexeme + "\\E",t.lexeme.length()) != null)
                    .findFirst();

            if(resToken.isPresent()){
                frontierCharIndex += resToken.get().lexeme.length();
                result.add(resToken.get());
                return result;
            }

            throw new NoSuchLexemeException(exp,frontierCharIndex);

        }

        private List<Token> getMatchedTokens(String nextTokenStr){

            List<Token> result = new ArrayList<>();

            if(lsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.LS_UNARY_OPERATOR,nextTokenStr));

            if(binaryOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.BINARY_OPERATOR,nextTokenStr));

            if(rsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.RS_UNARY_OPERATOR,nextTokenStr));

            return result;
        }

        private Optional<String> findOperator(){
            return operators.stream()
                    .filter(str -> scanner.findWithinHorizon("\\Q" + str + "\\E",str.length()) != null)
                    .findFirst();
        }

        private Optional<String> findFunction(){
            return functions.stream()
                    .filter(str -> scanner.findWithinHorizon(str,str.length()) != null)
                    .findFirst();

        }

        private Optional<String> findVariable(){
            if(variables != null)
                return variables.stream()
                                .filter(str -> scanner.findWithinHorizon(str,str.length()) != null)
                                .findFirst();

            return Optional.empty();
        }

        public void close(){
            scanner.close();
        }

    }
