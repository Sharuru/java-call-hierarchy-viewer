--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 9.6.14

-- Started on 2020-01-03 18:17:30

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 201 (class 1259 OID 1080613)
-- Name: meta_proto_gamma; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.meta_proto_gamma (
    id bigint NOT NULL,
    method_qualified_name text NOT NULL,
    method_path text,
    method_comment text,
    method_type text,
    method_callee_qualified_name text,
    method_callee_seq bigint,
    method_callee_class text,
    method_callee_method text
);


--
-- TOC entry 200 (class 1259 OID 1080611)
-- Name: meta_proto_gamma_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.meta_proto_gamma_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2186 (class 0 OID 0)
-- Dependencies: 200
-- Name: meta_proto_gamma_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.meta_proto_gamma_id_seq OWNED BY public.meta_proto_gamma.id;


--
-- TOC entry 2057 (class 2604 OID 1080616)
-- Name: meta_proto_gamma id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.meta_proto_gamma ALTER COLUMN id SET DEFAULT nextval('public.meta_proto_gamma_id_seq'::regclass);


--
-- TOC entry 2063 (class 2606 OID 1080621)
-- Name: meta_proto_gamma meta_proto_gamma_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.meta_proto_gamma
    ADD CONSTRAINT meta_proto_gamma_pkey PRIMARY KEY (id);


--
-- TOC entry 2058 (class 1259 OID 1088903)
-- Name: meta_proto_gamma_method_callee_class_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX meta_proto_gamma_method_callee_class_idx ON public.meta_proto_gamma USING btree (method_callee_class);


--
-- TOC entry 2059 (class 1259 OID 1088904)
-- Name: meta_proto_gamma_method_callee_method_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX meta_proto_gamma_method_callee_method_idx ON public.meta_proto_gamma USING btree (method_callee_method);


--
-- TOC entry 2060 (class 1259 OID 1088893)
-- Name: meta_proto_gamma_method_callee_qualified_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX meta_proto_gamma_method_callee_qualified_name_idx ON public.meta_proto_gamma USING btree (method_callee_qualified_name);


--
-- TOC entry 2061 (class 1259 OID 1080622)
-- Name: meta_proto_gamma_method_quailfied_name_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX meta_proto_gamma_method_quailfied_name_idx ON public.meta_proto_gamma USING btree (method_qualified_name);


-- Completed on 2020-01-03 18:17:30

--
-- PostgreSQL database dump complete
--

